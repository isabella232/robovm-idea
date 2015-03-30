/*
 * Copyright (C) 2015 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.idea.running;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.robovm.compiler.AppCompiler;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.Config;
import org.robovm.idea.RoboVmPlugin;

import java.util.Collection;

public class RoboVmRunConfiguration extends ModuleBasedConfiguration<RoboVmRunConfigurationSettings> implements RunConfigurationWithSuppressedDefaultDebugAction, RunConfigurationWithSuppressedDefaultRunAction, RunProfileWithCompileBeforeLaunchOption {
    public static enum TargetType {
        Simulator,
        Device,
        Console
    }

    private TargetType targetType;
    private Arch deviceArch;
    private String signingIdentity;
    private String provisioningProfile;
    private Arch simArch;
    private String simulatorName;
    private String moduleName;

    // these are used to pass information between
    // the compiler, the run configuration and the
    // runner. They are not persisted.
    private boolean isDebug;
    private Config config;
    private int debugPort;
    private AppCompiler compiler;
    private ConfigurationType type;

    public RoboVmRunConfiguration(ConfigurationType type, String name, RoboVmRunConfigurationSettings configurationModule, ConfigurationFactory factory) {
        super(name, configurationModule, factory);
        this.type = type;
    }

    @Override
    public Collection<Module> getValidModules() {
        return RoboVmPlugin.getRoboVmModules(getConfigurationModule().getProject());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        if(type instanceof RoboVmIOSConfigurationType) {
            return new RoboVmIOSRunConfigurationSettingsEditor();
        } else {
            return new RoboVmConsoleRunConfigurationSettingsEditor();
        }
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new RoboVmRunProfileState(environment);
    }

    @Override
    public ModuleBasedConfiguration clone() {
        return super.clone();
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);

        moduleName = JDOMExternalizerUtil.readField(element, "moduleName");
        String targetTypeStr = JDOMExternalizerUtil.readField(element, "targetType");
        targetType = targetTypeStr.length() == 0? null: TargetType.valueOf(targetTypeStr);
        String deviceArchStr = JDOMExternalizerUtil.readField(element, "deviceArch");
        deviceArch = deviceArchStr.length() == 0? null: Arch.valueOf(deviceArchStr);
        signingIdentity = JDOMExternalizerUtil.readField(element, "signingIdentity");
        provisioningProfile = JDOMExternalizerUtil.readField(element, "provisioningProfile");
        String simArchStr = JDOMExternalizerUtil.readField(element, "simArch");
        simArch = simArchStr.length() == 0? null: Arch.valueOf(simArchStr);
        simulatorName = JDOMExternalizerUtil.readField(element, "simulatorName");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);

        JDOMExternalizerUtil.writeField(element, "moduleName", moduleName);
        JDOMExternalizerUtil.writeField(element, "targetType", targetType == null? null: targetType.toString());
        JDOMExternalizerUtil.writeField(element, "deviceArch", deviceArch == null? null: deviceArch.toString());
        JDOMExternalizerUtil.writeField(element, "signingIdentity", signingIdentity);
        JDOMExternalizerUtil.writeField(element, "provisioningProfile", provisioningProfile);
        JDOMExternalizerUtil.writeField(element, "simArch", simArch == null? null: simArch.toString());
        JDOMExternalizerUtil.writeField(element, "simulatorName", simulatorName);
    }

    public void setDeviceArch(Arch deviceArch) {
        this.deviceArch = deviceArch;
    }

    public Arch getDeviceArch() {
        return deviceArch;
    }

    public void setSigningIdentity(String signingIdentity) {
        this.signingIdentity = signingIdentity;
    }

    public String getSigningIdentity() {
        return signingIdentity;
    }

    public void setProvisioningProfile(String provisioningProfile) {
        this.provisioningProfile = provisioningProfile;
    }

    public String getProvisioningProfile() {
        return provisioningProfile;
    }

    public void setSimArch(Arch simArch) {
        this.simArch = simArch;
    }

    public Arch getSimArch() {
        return simArch;
    }

    public void setSimulatorName(String simulatorName) {
        this.simulatorName = simulatorName;
    }

    public String getSimulatorName() {
        return simulatorName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setCompiler(AppCompiler compiler) {
        this.compiler = compiler;
    }

    public AppCompiler getCompiler() {
        return compiler;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
}