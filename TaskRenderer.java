import javax.swing.*;
import java.awt.*;

public class TaskRenderer extends JPanel implements ListCellRenderer<Task> {
    private JCheckBox checkBox = new JCheckBox();
    private JLabel label = new JLabel();

    public TaskRenderer() {
        setLayout(new BorderLayout());
        add(checkBox, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        checkBox.setSelected(value.isDone());
        label.setText(value.getName() + " (Due: " + value.getDueDate() + ", " + value.getCategory() + ")");

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            label.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            label.setForeground(list.getForeground());
        }
        return this;
    }
}