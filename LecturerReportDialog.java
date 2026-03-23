package com.university.system.view;
import javax.swing.*;
import java.awt.*;
public class LecturerReportDialog extends JDialog {
    public LecturerReportDialog(Frame parent, String staff) {
        super(parent, "Lecturer Report", true);
        setSize(400, 300);
        add(new JLabel("Report for Lecturer: " + staff));
    }
}
