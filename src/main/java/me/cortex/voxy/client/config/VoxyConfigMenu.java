package me.cortex.voxy.client.config;

import me.cortex.voxy.client.ClientSessionEvents;
import me.cortex.voxy.client.config.SodiumConfigBuilder.*;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import me.cortex.voxy.client.core.SSAO;
import me.cortex.voxy.client.core.util.IrisUtil;
import me.cortex.voxy.common.util.cpu.CpuLayout;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.ConfigState;
import net.caffeinemc.mods.sodium.api.config.option.OptionFlag;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

//Fabric Sodium finds this via the sodium:config_api_user entrypoint; NeoForge Sodium (e.g. under Sinytra Connector)
// only discovers entry points through this annotation or a neoforge.mods.toml property
@ConfigEntryPointForge("voxy")
public class VoxyConfigMenu implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder B) {
        if (!VoxyCommon.isAvailable()) return;//Dont even register the config if its not avalible

        var CFG = VoxyConfig.CONFIG;

        var cc = B.registerModOptions("voxy", "Voxy", VoxyCommon.MOD_VERSION)
                .setIcon(ResourceLocation.parse("voxy:icon.png"));

        final var RENDER_RELOAD = OptionFlag.REQUIRES_RENDERER_RELOAD.getId().toString();

        SodiumConfigBuilder.buildToSodium(B, cc, CFG::save, postOp->{
                    postOp.register("voxy:update_threads", ()->{
                        var instance = VoxyCommon.getInstance();
                        if (instance != null) {
                            instance.updateDedicatedThreads();
                        }
                    }, "voxy:enabled").register("voxy:iris_reload", ()->IrisUtil.reload());
                },
                new Page(Component.translatable("voxy.config.general"),
                        new Group(
                                new BoolOption(
                                        "voxy:enabled",
                                        Component.translatable("voxy.config.general.enabled"),
                                        ()->CFG.enabled, v->{
                                            CFG.enabled=v;
                                            //we need to special case enabled, since the render reload flag runs befor us and its quite important we get it right
                                            if (v && ClientSessionEvents.inSession) {//We should only load when we are in session
                                                VoxyCommon.createInstance();
                                            }
                                        })
                                        .setPostChangeRunner(c->{
                                            if (!c) {
                                                var vrsh = (IGetVoxyRenderSystem) Minecraft.getInstance().levelRenderer;
                                                if (vrsh != null) {
                                                    vrsh.voxy$shutdownRenderer();
                                                }
                                                VoxyCommon.shutdownInstance();
                                            }
                                        }).setPostChangeFlags(RENDER_RELOAD, "voxy:iris_reload").setEnabler(null)
                        ), new Group(
                                new IntOption(
                                        "voxy:thread_count",
                                        Component.translatable("voxy.config.general.serviceThreads"),
                                        ()->CFG.serviceThreads, v->CFG.serviceThreads=v,
                                        new Range(1, CpuLayout.getCoreCount(), 1))
                                        .setPostChangeFlags("voxy:update_threads"),
                                new BoolOption(
                                        "voxy:use_sodium_threads",
                                        Component.translatable("voxy.config.general.useSodiumBuilder"),
                                        ()->!CFG.dontUseSodiumBuilderThreads, v->CFG.dontUseSodiumBuilderThreads=!v)
                                        .setPostChangeFlags("voxy:update_threads", RENDER_RELOAD)
                        ), new Group(
                                new BoolOption(
                                        "voxy:ingest_enabled",
                                        Component.translatable("voxy.config.general.ingest"),
                                        ()->CFG.ingestEnabled, v->CFG.ingestEnabled=v)
                        )
                ).setEnabler("voxy:enabled"),
                new Page(Component.translatable("voxy.config.rendering"),
                        new Group(
                                new BoolOption(
                                        "voxy:rendering",
                                        Component.translatable("voxy.config.general.rendering"),
                                        ()->CFG.enableRendering, v->CFG.enableRendering=v)
                                        .setPostChangeRunner(c->{
                                            var vrsh = (IGetVoxyRenderSystem)Minecraft.getInstance().levelRenderer;
                                            if (vrsh != null) {
                                                if (c) {
                                                    vrsh.voxy$createRenderer();
                                                } else {
                                                    vrsh.voxy$shutdownRenderer();
                                                }
                                            }
                                        },"voxy:enabled", RENDER_RELOAD)
                                        .setPostChangeFlags("voxy:iris_reload")
                                        .setEnabler("voxy:enabled")
                        ), new Group(
                                new IntOption(
                                        "voxy:subdivsize",
                                        Component.translatable("voxy.config.general.subDivisionSize"),
                                        ()->subDiv2ln(CFG.subDivisionSize), v->CFG.subDivisionSize=ln2subDiv(v),
                                        new Range(0, SUBDIV_IN_MAX, 1))
                                        .setFormatter(v->Component.literal(Integer.toString(Math.round(ln2subDiv(v)))))
                                        .setImpact(OptionImpact.HIGH),
                                new IntOption(
                                        "voxy:render_distance",
                                        Component.translatable("voxy.config.general.renderDistance"),
                                        ()->Math.round(CFG.sectionRenderDistance*16), v->CFG.sectionRenderDistance=((float)v)/16,
                                        new Range(10/*1*16*/, 64*16, 1))
                                        //The value is stored as a float with respect to the size of top level lods, it its increment is a fraction with respect to the size of the bottom level lod
                                        // the value is displayed as a chunk render distance
                                        .setFormatter(v->Component.literal(Integer.toString(v*2)))
                                        .setPostChangeRunner(c->{
                                            var vrsh = (IGetVoxyRenderSystem)Minecraft.getInstance().levelRenderer;
                                            if (vrsh != null) {
                                                var vrs = vrsh.voxy$getRenderSystem();
                                                if (vrs != null) {
                                                    //CFG.sectionRenderDistance == c/16
                                                    vrs.setRenderDistance(CFG.sectionRenderDistance);
                                                }
                                            }
                                        }, "voxy:rendering", RENDER_RELOAD)
                                        .setImpact(OptionImpact.MEDIUM)
                        ), new Group(
                                new BoolOption(
                                        "voxy:render_fog",
                                        Component.translatable("voxy.config.general.render_fog"),
                                        Component.translatable("voxy.config.general.render_fog.tooltip"),
                                        ()->CFG.renderVoxyFog, v->CFG.renderVoxyFog=v)
                                        .setImpact(OptionImpact.LOW)
                                        .setPostChangeFlags(RENDER_RELOAD),
                                new IntOption(
                                        "voxy:sky_fog_distance",
                                        Component.literal("Sky fog distance"),
                                        Component.literal("Higher distance, sharper sky fog"),
                                        ()->CFG.skyFogDistance, v->CFG.skyFogDistance=v,
                                        new Range(16, 512, 16)),
                                new IntOption(
                                        "voxy:fog_intensity",
                                        Component.literal("Fog intensity"),
                                        Component.literal("Multiplier for terrain fog opacity. 0.0 = off, 1.0 = vanilla, 3.0 = triple"),
                                        ()->Math.round(CFG.fogIntensity*100), v->CFG.fogIntensity=v/100.0f,
                                        new Range(0, 300, 10))
                                        .setFormatter(v->Component.literal(String.format("%.1f", v/100.0f))),
                                new IntOption(
                                        "voxy:fog_density",
                                        Component.literal("Fog curve"),
                                        Component.literal("Shape of the fog curve. 0.0 = linear, higher values push fog towards the far end"),
                                        ()->Math.round(CFG.fogDensity*10), v->CFG.fogDensity=v/10.0f,
                                        new Range(0, 50, 1))
                                        .setFormatter(v->Component.literal(String.format("%.1f", v/10.0f))),
                                new EnumOption<>("voxy:ssao_mode",
                                        SSAO.SSAOMode.class,
                                        Component.translatable("voxy.config.general.ssao_mode"),
                                        Component.translatable("voxy.config.general.ssao_mode.tooltip"),
                                        ()->CFG.getSSAOMode(), v->CFG.setSSAOMode(v))
                                        .setNameProvider(v->Component.translatable("voxy.config.general.ssao_mode."+v.name().toLowerCase(java.util.Locale.ROOT)))
                                        .setImpact(OptionImpact.MEDIUM)//TODO make it on igpus this is high
                                        .setPostChangeFlags(RENDER_RELOAD)
                        ).setEnablerInherit(s->!IrisUtil.irisShadersEnabledInConfig(), ConfigState.UPDATE_ON_REBUILD),
                        new Group(
                                new BoolOption(
                                        "voxy:adapt_cloud_distance",
                                        Component.literal("Adapt cloud distance"),
                                        Component.literal("Extends the cloud distance according to the current render distance. It's automatically capped at 256"),
                                        ()->CFG.adaptCloudDistance, v->CFG.adaptCloudDistance=v)
                                        .setImpact(OptionImpact.LOW),
                                new IntOption(
                                        "voxy:cloud_distance",
                                        Component.literal("Cloud distance"),
                                        Component.literal("Cloud render distance in chunks"),
                                        ()->CFG.cloudDistance, v->CFG.cloudDistance=v,
                                        new Range(0, 2048, 2))
                                        .setFormatter(v->Component.literal(v<1?"Default":Integer.toString(v)))
                                        .setImpact(OptionImpact.VARIES)
                                        .setEnablerInherit(s->!s.readBooleanOption(ADAPT_CLOUD_ID), "voxy:adapt_cloud_distance")
                        )
                ).setEnablerAND("voxy:enabled", "voxy:rendering"));

    }


    private static final ResourceLocation ADAPT_CLOUD_ID = ResourceLocation.parse("voxy:adapt_cloud_distance");

    private static final int SUBDIV_IN_MAX = 100;
    private static final double SUBDIV_MIN = 28;
    private static final double SUBDIV_MAX = 256;
    private static final double SUBDIV_CONST = Math.log(SUBDIV_MAX/SUBDIV_MIN)/Math.log(2);

    //In range is 0->200
    //Out range is 28->256
    private static float ln2subDiv(int in) {
        return (float) (SUBDIV_MIN*Math.pow(2, SUBDIV_CONST*((double)in/SUBDIV_IN_MAX)));
    }

    //In range is ... any?
    //Out range is 0->200
    private static int subDiv2ln(float in) {
        return (int) (((Math.log(((double)in)/SUBDIV_MIN)/Math.log(2))/SUBDIV_CONST)*SUBDIV_IN_MAX);
    }
}
