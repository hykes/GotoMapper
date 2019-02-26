package com.github.hykes;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

/**
 * @author hehaiyangwork@gmail.com
 * @date 2019-02-26 11:40:00
 */
public class HelloAction extends AnAction {
    public HelloAction() {
        super("Hello");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        Messages.showMessageDialog(project, "Hello world!", "Greeting", Messages.getInformationIcon());
    }
}

