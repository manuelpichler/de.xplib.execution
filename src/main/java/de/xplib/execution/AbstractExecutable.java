/**
 * This file is part of the simple java execution helper library.
 *
 * Copyright (c) 2010, Manuel Pichler <mapi@phpmd.org>.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *   * Neither the name of Manuel Pichler nor the names of his
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author    Manuel Pichler <mapi@phpmd.org>
 * @copyright 2010 Manuel Pichler. All rights reserved.
 * @license   http://www.opensource.org/licenses/bsd-license.php BSD License
 * @version   SVN: $Id$
 * @link      http://phpmd.org
 */

package de.xplib.execution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract base implementation of the {@link Executable} interface.
 *
 * @author    Manuel Pichler <mapi@phpmd.org>
 * @copyright 2010 Manuel Pichler. All rights reserved.
 * @license   http://www.opensource.org/licenses/bsd-license.php BSD License
 * @version   SVN: $Id$
 * @link      http://phpmd.org
 */
public abstract class AbstractExecutable implements Executable {

    /**
     * List of strings representing the final command line string.
     */
    private List<String> command;

    /**
     * Exit code returned from the command line tool.
     */
    private Integer exitCode = null;

    /**
     * Input stream representing STDERR of the underlying process.
     */
    private InputStream stderr = null;

    private List<Integer> validExitCodes = new ArrayList<Integer>();

    /**
     * Constructs a new executable for the given executable name.
     *
     * @param command Name of an executable cli tool.
     */
    public AbstractExecutable(String command) {
        this(new ArrayList<String>(Arrays.asList(new String[] {command})));
    }

    /**
     * Constructs a new executable instance.
     *
     * @param command List of strings that build up the command line string.
     */
    public AbstractExecutable(List<String> command) {
        this.command = command;
    }

    /**
     * Constructs a new executable instance.
     *
     * @param executable A different/preconfigured executable.
     */
    public AbstractExecutable(Executable executable) {
        this(executable.getCommandLine());
        
        this.validExitCodes = executable.getValidExitCodes();
    }

    /**
     * Returns a list with integer values representing valid exit codes for the
     * wrapped cli script.
     *
     * @return List of valid exit codes.
     */
    @Override
    public List<Integer> getValidExitCodes() {
        if (this.validExitCodes.isEmpty()) {
            return new ArrayList<Integer>();
        }
        return this.validExitCodes;
    }

    /**
     * Returns a list with all configured fragments that build up the later
     * executed command.
     *
     * @return List<String>
     */
    @Override
    public List<String> getCommandLine() {
        return this.command;
    }

    /**
     * Adds the given string to the arguments for the cli command string.
     *
     * @param argument Simple string argument.
     *
     * @return The entire executable.
     */
    @Override
    public Executable addArgument(String argument) {
        this.command.add(argument);
        return this;
    }

    /**
     * Adds the given argument to the arguments for the cli command string.
     *
     * @param argument A argument implementation.
     *
     * @return The entire executable.
     */
    @Override
    public Executable addArgument(Argument argument) {
        return argument.toArgument(this);
    }

    /**
     * Adds a regular exit code for the underlying cli script.
     *
     * @param regularExitCode A regular/none error exit code.
     *
     * @return The entire executable.
     */
    @Override
    public Executable addRegularExitCode(Integer regularExitCode) {
        if (!this.validExitCodes.contains(regularExitCode)) {
            this.validExitCodes.add(regularExitCode);
        }
        return this;
    }

    /**
     * Template method that will be called by this class' exec method.
     *
     * @param proc The native process instance representing the underlying,
     *        running command line application.
     *
     * @return The exit code returned by the cli tool.
     * @throws InterruptedException If the process execution fails.
     */
    protected abstract int doExecute(Process process)
            throws InterruptedException;

    /**
     * Executes the underlying command line tool. If the execution failes, this
     * method will throw an exception of type {@link ExecutionException}.
     *
     * @throws ExecutionException When the execution of the underlying command
     *         line tool failes.
     */
    @Override
    public void exec() throws ExecutionException {
        this.validate();
        try {
            this.exitCode = this.doExecute(this.getProcess());

            if (!this.validExitCodes.contains(this.exitCode)) {
                throw new ExecutionException(this.getStderrText());
            }
        } catch (IOException ex) {
            throw new Error(ex);
        } catch (InterruptedException ex) {
            throw new Error(ex);
        }
    }

    /**
     * Returns the exit code returned by the underlying command line tool or
     * throws an exception when the command line tool is still running.
     *
     * @return The cli exit code.
     * @throws ExecutionException When the underlying command line tool is still
     *         running.
     */
    @Override
    public int exitCode() {
        if (this.exitCode == null) {
            throw new ExecutionException("Process still running...");
        }
        return this.exitCode;
    }

    /**
     * Returns a native process for the underlying command line application.
     *
     * @return The native process.
     * @throws
     */
    private Process getProcess() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(this.command);
        Process process = builder.start();

        this.stderr = process.getErrorStream();

        return process;
    }

    /**
     * Checks if the configured executable exists or is not executable. It will
     * throw an exception when one of these tests fails.
     *
     * @throws ExecutionException
     */
    private void validate() throws ExecutionException {
        this.validateExecutableExists();
    }

    /**
     * Tests if the configured executable really exists.
     *
     * @throws ExecutableNotFoundException When the configured executable does
     *         not exist.
     */
    private void validateExecutableExists() throws ExecutableNotFoundException {
        if (this.command.isEmpty()) {
            throw new ExecutableNotFoundException();
        }
        if (!new File(this.command.get(0)).exists()) {
            throw new ExecutableNotFoundException(this.command.get(0));
        }
    }

    private void validateExecutableIsExecutable() {
        try {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkExec(this.command.get(0));
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private String getStderrText() throws IOException {
        if (this.stderr == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        char[] c = new char[1024];

        InputStreamReader reader = new InputStreamReader(this.stderr);
        while (reader.read(c, 0, 1024) >= 0) {
            sb.append(new String(c));
        }
        return sb.toString().trim();
    }
}
