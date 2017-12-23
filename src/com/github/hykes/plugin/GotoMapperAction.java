package com.github.hykes.plugin;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.psi.xml.XmlFile;

/**
 * 查找 mapper
 *
 * @author hehaiyangwork@gmail.com
 * @date 2017/12/23
 */
public class GotoMapperAction extends AnAction implements DumbAware {

    public GotoMapperAction() {
        super(AllIcons.Icon_small);
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        String text = editor.getSelectionModel().getSelectedText();
        if (text == null || "".equals(text)) {
            e.getPresentation().setEnabledAndVisible(false);
        } else {
            e.getPresentation().setEnabledAndVisible(true);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.showDumbModeNotification("GotoMapper plugin is not available during indexing");
            return;
        }

        // 选中的文本
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        String text = editor.getSelectionModel().getSelectedText();

        // 当前文件
        PsiFile currentEditorFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        String className = currentEditorFile.getName();

        // 获取 mapper 文件名称
        if (!className.endsWith("Dao.java")) {
            return ;
        }
        String mapperName = className.replace("Dao.java", "Mapper.xml");

        // 查找名称为 mapper 文件
        PsiFile[] files = PsiShortNamesCache.getInstance(project).getFilesByName(mapperName);
        if (files.length == 0) {
            NotifyUtil.notice("not found mapper file !", MessageType.WARNING);
            return ;
        }
        XmlFile xmlFile = (XmlFile) files[0];
        //获取mapper xml字符串
        String xml = xmlFile.getDocument().getText();
        // 判断mapper是否存id="methodName"的sql, 存在就打开对应的mapper xml
        if (StringUtil.isNotEmpty(xml) && xml.contains("id=\"" + text + "\"")) {
            toMapper(project, text, files[0].getVirtualFile(), xml);
        }
    }

    /**
     * 进入mapper
     * @param project
     * @param methodName
     * @param mapperFile
     * @param xml
     */
    private void toMapper(Project project, String methodName, VirtualFile mapperFile, String xml) {
        // 打开xml文件
        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(project, mapperFile);
        Editor editor = FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true);

        // 获取sql所在的行数，这里用了比较笨的方法。api找了很久没找到有什么方法可以获取行号，希望有大神指点
        String[] split = xml.split("\n");
        int lineNumber = 0;
        for (int i = 0; i < split.length; i++) {
            String line = split[i];
            if (StringUtil.isNotEmpty(line) && line.contains(methodName)) {
                lineNumber = i;
                break;
            }
        }
        // 定位到对应的sql
        CaretModel caretModel = editor.getCaretModel();
        LogicalPosition logicalPosition = caretModel.getLogicalPosition();
        logicalPosition.leanForward(true);
        LogicalPosition logical = new LogicalPosition(lineNumber, logicalPosition.column);
        caretModel.moveToLogicalPosition(logical);
        SelectionModel selectionModel = editor.getSelectionModel();
        selectionModel.selectLineAtCaret();
    }

}