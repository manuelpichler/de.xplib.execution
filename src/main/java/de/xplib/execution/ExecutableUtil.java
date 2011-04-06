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

/**
 * Simple utility class to find available executables.
 *
 * Utility class that can be used to find executables available in one of the
 * user's PATH directories.
 *
 * @author    Manuel Pichler <mapi@phpmd.org>
 * @copyright 2010 Manuel Pichler. All rights reserved.
 * @license   http://www.opensource.org/licenses/bsd-license.php BSD License
 * @version   SVN: $Id$
 * @link      http://phpmd.org
 */
public final class ExecutableUtil {

    /**
     * System environment variable name that normally contains the user's path
     * settings. Works at least for Linux and Windows.
     */
    private static final String PATH_ENVIRONMENT_VARIABLE = "PATH";

    /**
     * Common extensions of executables.
     */
    private static final String[] EXECUTABLE_EXTENSIONS = {
        ".bat", ".exe", ".sh", ".php"
    };

    /**
     * Empty ctor, this is just a utility class with static methods.
     */
    private ExecutableUtil() {

    }

    /**
     * Searches for a binary, shell script or batch file that has the given
     * local name and exists in one of the user's PATH directories. This method
     * will return <b>null</b> when no binary was found.
     *
     * @param localName Local script file name without a file type extension.
     *
     * @return Executable
     */
    public static Executable findExecutableOnUsersPath(String localName) {
        for (String pathName : getUsersPath()) {
            String fileName   = pathName + File.separator + localName;
            String executable = existsAndIsExecutable(fileName);
            if (executable != null) {
                return new DefaultExecutable(executable);
            }
        }
        return null;
    }

    /**
     * Tests if a file  fileName (directory + local name) plus one of the
     * configured extensions exists and is executable. If such a file exists
     * this method will return the full qualified file name, otherwise it
     * returns <b>null</b>.
     *
     * @param fileName Qualified file name, but without and extension.
     *
     * @return String
     */
    private static String existsAndIsExecutable(String fileName) {
        for (String extension : EXECUTABLE_EXTENSIONS) {
            String executable = existsAndIsExecutable(fileName, extension);
            if (executable != null) {
                return executable;
            }
        }
        return existsAndIsExecutable(fileName, "");
    }

    /**
     * Tests if a file with the given file name + the given extension exists
     * and is executable. If such a file exists this method will return the
     * full qualified file name, otherwise it returns <b>null</b>.
     *
     * @param fileName Qualified file name, but without and extension.
     * @param extension The file extension, prefixed with a ".".
     *
     * @return String
     */
    private static String existsAndIsExecutable(
            String fileName,
            String extension
    ) {
        File file = new File(fileName + extension);
        if (file.exists() && isExecutable(file)) {
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * Tests if the given <b>file</b> is executable.
     *
     * @param file The file to test.
     *
     * @return boolean
     */
    private static boolean isExecutable(File file) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkExec(file.getAbsolutePath());
            } catch (SecurityException se) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an array with all environment paths of the current user.
     *
     * @return String[]
     */
    private static String[] getUsersPath() {
        String env = System.getenv(PATH_ENVIRONMENT_VARIABLE);
        if (env == null || env.trim().equals("")) {
            return new String[0];
        }
        return env.split(File.pathSeparator);
    }
}
