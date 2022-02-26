package org.zebias.lsautoconfig.ui.log;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TextAreaPrint extends PrintStream {//可以正常解析utf8和gbk码

    private TextArea console;

    public TextAreaPrint(TextArea console) {
        super(new ByteArrayOutputStream());
        this.console = console;
    }



    @Override
    public void write(byte[] buf, int off, int len) {
        print(new String(buf, off, len));
    }

    @Override
    public void print(String s) {
        // 这里为什么使用这种方式？下一个技术点讲解
        Platform.runLater(() -> console.appendText(s));
    }

    public void clear() {
        Platform.runLater(() -> console.clear());
    }
}