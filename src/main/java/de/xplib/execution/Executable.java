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
 * Base interface for an executable.
 *
 * @author    Manuel Pichler <mapi@phpmd.org>
 * @copyright 2010 Manuel Pichler. All rights reserved.
 * @license   http://www.opensource.org/licenses/bsd-license.php BSD License
 * @version   SVN: $Id$
 * @link      http://phpmd.org
 */
public interface Executable {

    /**
     * Executes the underlying command line tool. If the execution failes, this
     * method will throw an exception of type {@link ExecutionException}.
     *
     * @throws ExecutionException When the execution of the underlying command
     *         line tool failes.
     */
    void exec();

    /**
     * Returns a list with integer values representing valid exit codes for the
     * wrapped cli script.
     *
     * @return List of valid exit codes.
     */
    List<Integer> getValidExitCodes();

    /**
     * Returns a list with all configured fragments that build up the later
     * executed command.
     *
     * @return List<String>
     */
    List<String> getCommandLine();

    /**
     * Adds the given string to the arguments for the cli command string.
     *
     * @param argument Simple string argument.
     *
     * @return The entire executable.
     */
    Executable addArgument(String argument);

    /**
     * Adds the given argument to the arguments for the cli command string.
     *
     * @param argument A argument implementation.
     *
     * @return The entire executable.
     */
    Executable addArgument(Argument argument);

    /**
     * Adds a regular exit code for the underlying cli script.
     *
     * @param regularExitCode A regular/none error exit code.
     *
     * @return The entire executable.
     */
    Executable addRegularExitCode(Integer regularExitCode);

    /**
     * Returns the exit code returned by the underlying command line tool or
     * throws an exception when the command line tool is still running.
     *
     * @return The cli exit code.
     * @throws ExecutionException When the underlying command line tool is still
     *         running.
     */
    int exitCode();
}
