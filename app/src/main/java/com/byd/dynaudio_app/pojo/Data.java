// Data.java

package com.byd.dynaudio_app.pojo;

import java.util.List;

/**
 * 返参
 */
public class Data<T> {
    /**
     * 专辑、歌曲信息
     */
    private List<ModuleData> moduleData;
    /**
     * 模块基本信息
     */
    private ModuleInfo moduleInfo;
    /**
     * 模块是否展示
     */
    private boolean showFlag;

    public List<ModuleData> getModuleData() { return moduleData; }
    public void setModuleData(List<ModuleData> value) { this.moduleData = value; }

    public ModuleInfo getModuleInfo() { return moduleInfo; }
    public void setModuleInfo(ModuleInfo value) { this.moduleInfo = value; }

    public boolean getShowFlag() { return showFlag; }
    public void setShowFlag(boolean value) { this.showFlag = value; }

    @Override
    public String toString() {
        return "Data{" +
                "moduleData=" + moduleData +
                ", moduleInfo=" + moduleInfo +
                ", showFlag=" + showFlag +
                '}';
    }
}