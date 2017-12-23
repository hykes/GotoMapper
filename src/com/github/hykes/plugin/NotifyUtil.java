package com.github.hykes.plugin;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;

/**
 * @author hehaiyangwork@gmail.com
 * @date 2017/12/20
 */
public class NotifyUtil {

    private final static NotificationGroup GROUP_DISPLAY_ID_INFO =
            new NotificationGroup("GotoMapper notification group",
                    NotificationDisplayType.BALLOON, true);

    public static void notice(String title, String content, MessageType type){
        Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(title, content, type.toNotificationType(), null);
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Notifications.Bus.notify(notification, projects[0]);
    }

    public static void notice(String content, MessageType type){
        Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(content, type);
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Notifications.Bus.notify(notification, projects[0]);
    }

}
