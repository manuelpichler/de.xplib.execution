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

import java.util.List;

/**
 * This class allows execution without blocking.
 *
 * This class is a decorator for executables that runs an executable into a
 * separate thread, so that the execution will not block the application.
 *
 * @author    Manuel Pichler <mapi@phpmd.org>
 * @copyright 2010 Manuel Pichler. All rights reserved.
 * @license   http://www.opensource.org/licenses/bsd-license.php BSD License
 * @version   SVN: $Id$
 * @link      http://phpmd.org
 */
public class NonBlockingExecutable extends Thread implements Executable {

    /**
     * The decorated executable instance.
     */
    private Executable executable;

    /**
     * The execution priority for this executable.
     */
    private static final int DEFAULT_PIORITY = MIN_PRIORITY;

    /**
     * Constructs a new non blocking decorator for the given executable.
     *
     * @param executable An executable instance to decorate.
     */
    public NonBlockingExecutable(Executable executable) {
        this.executable = executable;
        this.setPriority(DEFAULT_PIORITY);
    }

    /**
     * Returns a list with integer values representing valid exit codes for the
     * wrapped cli script.
     *
     * @return List of valid exit codes.
     */
    @Override
    public List<Integer> getValidExitCodes() {
        return this.executable.getValidExitCodes();
    }

    /**
     * Returns a list with Strings representing the final command line string.
     *
     * @return List of string fragments representing the command line.
     */
    @Override
    public List<String> getCommandLine() {
        return this.executable.getCommandLine();
    }

    /**
     * Adds the given string to the command line arguments of this executable.
     *
     * @param argument String argument that should be part of the command line
     *        invocation.
     *
     * @return The entire executable instances, allows nice chaining.
     */
    @Override
    public Executable addArgument(String argument) {
        this.executable.addArgument(argument);
        return this;
    }

    /**
     * Adds the given {@link Argument} to the command line arguments of this
     * executable.
     *
     * @param argument Any valid argument implementation.
     *
     * @return The entire executable instances, allows nice chaining.
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
        this.executable.addRegularExitCode(regularExitCode);
        return this;
    }

    /**
     * Starts the execution of the underlying command line tool.
     *
     * @throws ExecutionException When something the underlying command line
     *         tool fails for some reasone.
     */
    @Override
    public void exec() throws ExecutionException {
        this.start();
    }

    /**
     * Returns the exit code of the decorated executable.
     *
     * @return Exit code returned by the underlying executable.
     */
    @Override
    public int exitCode() {
        return this.executable.exitCode();
    }

    /**
     * Method inherit from {@link Runnable}
     */
    @Override
    public void run() {
        this.executable.exec();
    }
}
