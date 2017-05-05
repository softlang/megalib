/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.cli;

import java.util.Objects;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class CommandLineArguments {

    private String filePath;

    private String type;

    public CommandLineArguments(String filePath, String type) {
        this.filePath = filePath;
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.filePath);
        hash = 47 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CommandLineArguments other = (CommandLineArguments) obj;
        if (!Objects.equals(this.filePath, other.filePath)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }

}
