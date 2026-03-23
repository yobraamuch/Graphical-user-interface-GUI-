package com.university.system.view;
import javax.swing.*;
import java.awt.*;
import com.university.system.model.Student;
public class EnrollmentDialog extends JDialog {
    public EnrollmentDialog(Frame parent, Student student) {
        super(parent, "Enrollment", true);
        setSize(400, 300);
        add(new JLabel("Enrollment for: " + student.getName()));
    }
}
