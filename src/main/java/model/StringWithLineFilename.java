package model;

public class StringWithLineFilename {
    private String content;
    private String filename;
    private Integer line;

    public StringWithLineFilename(String content, String filename, Integer line) {
        this.content = content;
        this.filename = filename;
        this.line = line;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }
}
