package org.softlang.java.preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class MegaLPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public MegaLPreference() {
        super(GRID);
    }

    public void createFieldEditors() {
        addField(new FileFieldEditor("Graphvizz", "Graphvizz bin/dot path:", getFieldEditorParent()));
    }

    @Override
    public void init(IWorkbench workbench) {
        // second parameter is typically the plug-in id
        setPreferenceStore(new ScopedPreferenceStore(ConfigurationScope.INSTANCE, "org.softlang.java"));
        setDescription("Settings for the MegaL GraphView");
    }

}