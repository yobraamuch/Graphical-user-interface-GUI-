package com.university.system.view;
import javax.swing.*;
import java.awt.*;
import com.university.system.model.Student;
public class ResultSlipDialog extends JDialog {
    public ResultSlipDialog(Frame parent, String reg) {
        super(parent, "Result Slip", true);
        setSize(400, 300);
        add(new JLabel("Result Slip for: " + reg));
    }
}
