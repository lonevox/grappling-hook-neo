package com.lonevox.grapplinghookneo.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class GrappleConfig {
    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    public static final Client CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;

    private static volatile Config cachedServer = new Config();
    private static volatile ClientConfig cachedClient = new ClientConfig();
    private static volatile boolean serverLoaded;
    private static volatile boolean clientLoaded;

    // Kept for compatibility with existing login sync packet.
    private static volatile Config serverOverride;

    private static final String CUSTOM_DEFAULT_COMMENT = "Value used when creating a new non-modified grappling hook.";
    private static final String CUSTOM_ENABLED_COMMENT = "Whether this value can be changed in the grappling hook modifier: 0 = always enabled, 1 = enabled after limits upgrade, 2 = always disabled.";
    private static final String CUSTOM_MAX_COMMENT = "Maximum value in the grappling hook modifier before the limits upgrade.";
    private static final String CUSTOM_MAX_UPGRADED_COMMENT = "Maximum value in the grappling hook modifier after the limits upgrade.";
    private static final String CUSTOM_MIN_COMMENT = "Minimum value in the grappling hook modifier before the limits upgrade.";
    private static final String CUSTOM_MIN_UPGRADED_COMMENT = "Minimum value in the grappling hook modifier after the limits upgrade.";

    static {
        Pair<Server, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER = serverPair.getLeft();
        SERVER_SPEC = serverPair.getRight();

        Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    private GrappleConfig() {
    }

    public static Config getConf() {
        Config override = serverOverride;
        if (override != null) {
            return override;
        }

        if (SERVER_SPEC.isLoaded() != serverLoaded) {
            refreshServer();
        }

        return cachedServer;
    }

    public static ClientConfig getClientConf() {
        if (CLIENT_SPEC.isLoaded() != clientLoaded) {
            refreshClient();
        }

        return cachedClient;
    }

    public static void setServerOptions(Config newServerOptions) {
        serverOverride = newServerOptions;
    }

    public static void onModConfigLoading(ModConfigEvent.Loading event) {
        handleConfigEvent(event.getConfig());
    }

    public static void onModConfigReloading(ModConfigEvent.Reloading event) {
        handleConfigEvent(event.getConfig());
    }

    private static void handleConfigEvent(ModConfig config) {
        if (config.getSpec() == SERVER_SPEC) {
            refreshServer();
        } else if (config.getSpec() == CLIENT_SPEC) {
            refreshClient();
        }
    }

    private static synchronized void refreshServer() {
        if (!SERVER_SPEC.isLoaded()) {
            cachedServer = new Config();
            serverLoaded = false;
            return;
        }

        cachedServer = SERVER.toConfig();
        serverLoaded = true;
    }

    private static synchronized void refreshClient() {
        if (!CLIENT_SPEC.isLoaded()) {
            cachedClient = new ClientConfig();
            clientLoaded = false;
            return;
        }

        cachedClient = CLIENT.toConfig();
        clientLoaded = true;
    }

    private static ModConfigSpec.BooleanValue defineBool(ModConfigSpec.Builder builder, String key, boolean defaultValue, String comment) {
        return builder.comment(comment).define(key, defaultValue);
    }

    private static ModConfigSpec.IntValue defineInt(ModConfigSpec.Builder builder, String key, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).defineInRange(key, defaultValue, min, max);
    }

    private static ModConfigSpec.DoubleValue defineDouble(ModConfigSpec.Builder builder, String key, double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).defineInRange(key, defaultValue, min, max);
    }

    private static ModConfigSpec.ConfigValue<String> defineString(ModConfigSpec.Builder builder, String key, String defaultValue, String comment) {
        return builder.comment(comment).define(key, defaultValue);
    }

    private static final class BooleanCustomizationValue {
        private final ModConfigSpec.BooleanValue defaultValue;
        private final ModConfigSpec.IntValue enabled;

        private BooleanCustomizationValue(ModConfigSpec.Builder builder, String name, Config.GrapplingHook.Custom.BooleanCustomizationOption defaults) {
            builder.push(name);
            this.defaultValue = defineBool(builder, "default_value", defaults.default_value, CUSTOM_DEFAULT_COMMENT);
            this.enabled = defineInt(builder, "enabled", defaults.enabled, 0, 2, CUSTOM_ENABLED_COMMENT);
            builder.pop();
        }

        private Config.GrapplingHook.Custom.BooleanCustomizationOption toConfig() {
            return new Config.GrapplingHook.Custom.BooleanCustomizationOption(this.defaultValue.get(), this.enabled.get());
        }
    }

    private static final class DoubleCustomizationValue {
        private final ModConfigSpec.DoubleValue defaultValue;
        private final ModConfigSpec.IntValue enabled;
        private final ModConfigSpec.DoubleValue max;
        private final ModConfigSpec.DoubleValue maxUpgraded;
        private final ModConfigSpec.DoubleValue min;
        private final ModConfigSpec.DoubleValue minUpgraded;

        private DoubleCustomizationValue(ModConfigSpec.Builder builder, String name, Config.GrapplingHook.Custom.DoubleCustomizationOption defaults) {
            builder.push(name);
            this.defaultValue = defineDouble(builder, "default_value", defaults.default_value, -Double.MAX_VALUE, Double.MAX_VALUE, CUSTOM_DEFAULT_COMMENT);
            this.enabled = defineInt(builder, "enabled", defaults.enabled, 0, 2, CUSTOM_ENABLED_COMMENT);
            this.max = defineDouble(builder, "max", defaults.max, -Double.MAX_VALUE, Double.MAX_VALUE, CUSTOM_MAX_COMMENT);
            this.maxUpgraded = defineDouble(builder, "max_upgraded", defaults.max_upgraded, -Double.MAX_VALUE, Double.MAX_VALUE, CUSTOM_MAX_UPGRADED_COMMENT);
            this.min = defineDouble(builder, "min", defaults.min, -Double.MAX_VALUE, Double.MAX_VALUE, CUSTOM_MIN_COMMENT);
            this.minUpgraded = defineDouble(builder, "min_upgraded", defaults.min_upgraded, -Double.MAX_VALUE, Double.MAX_VALUE, CUSTOM_MIN_UPGRADED_COMMENT);
            builder.pop();
        }

        private Config.GrapplingHook.Custom.DoubleCustomizationOption toConfig() {
            return new Config.GrapplingHook.Custom.DoubleCustomizationOption(
                    this.defaultValue.get(),
                    this.enabled.get(),
                    this.max.get(),
                    this.maxUpgraded.get(),
                    this.min.get(),
                    this.minUpgraded.get()
            );
        }
    }

    public static final class Server {
        private final GrapplingHook grapplinghook;
        private final LongFallBoots longfallboots;
        private final EnderStaff enderstaff;
        private final Enchantments enchantments;
        private final Other other;

        private Server(ModConfigSpec.Builder builder) {
            Config defaults = new Config();

            this.grapplinghook = new GrapplingHook(builder, defaults.grapplinghook);
            this.longfallboots = new LongFallBoots(builder, defaults.longfallboots);
            this.enderstaff = new EnderStaff(builder, defaults.enderstaff);
            this.enchantments = new Enchantments(builder, defaults.enchantments);
            this.other = new Other(builder, defaults.other);
        }

        private Config toConfig() {
            Config config = new Config();
            config.grapplinghook = this.grapplinghook.toConfig();
            config.longfallboots = this.longfallboots.toConfig();
            config.enderstaff = this.enderstaff.toConfig();
            config.enchantments = this.enchantments.toConfig();
            config.other = this.other.toConfig();
            return config;
        }

        private static final class GrapplingHook {
            private final Custom custom;
            private final Blocks blocks;
            private final Other other;

            private GrapplingHook(ModConfigSpec.Builder builder, Config.GrapplingHook defaults) {
                builder.comment("Settings for grappling hook behavior.").push("grapplinghook");
                this.custom = new Custom(builder, defaults.custom);
                this.blocks = new Blocks(builder, defaults.blocks);
                this.other = new Other(builder, defaults.other);
                builder.pop();
            }

            private Config.GrapplingHook toConfig() {
                Config.GrapplingHook config = new Config.GrapplingHook();
                config.custom = this.custom.toConfig();
                config.blocks = this.blocks.toConfig();
                config.other = this.other.toConfig();
                return config;
            }

            private static final class Custom {
                private final Rope rope;
                private final HookThrower hookthrower;
                private final Motor motor;
                private final Swing swing;
                private final EnderStaff enderstaff;
                private final Forcefield forcefield;
                private final Magnet magnet;
                private final DoubleHook doublehook;
                private final Rocket rocket;

                private Custom(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom defaults) {
                    builder.comment("Options used by grappling hook modifier upgrades.").push("custom");
                    this.rope = new Rope(builder, defaults.rope);
                    this.hookthrower = new HookThrower(builder, defaults.hookthrower);
                    this.motor = new Motor(builder, defaults.motor);
                    this.swing = new Swing(builder, defaults.swing);
                    this.enderstaff = new EnderStaff(builder, defaults.enderstaff);
                    this.forcefield = new Forcefield(builder, defaults.forcefield);
                    this.magnet = new Magnet(builder, defaults.magnet);
                    this.doublehook = new DoubleHook(builder, defaults.doublehook);
                    this.rocket = new Rocket(builder, defaults.rocket);
                    builder.pop();
                }

                private Config.GrapplingHook.Custom toConfig() {
                    Config.GrapplingHook.Custom config = new Config.GrapplingHook.Custom();
                    config.rope = this.rope.toConfig();
                    config.hookthrower = this.hookthrower.toConfig();
                    config.motor = this.motor.toConfig();
                    config.swing = this.swing.toConfig();
                    config.enderstaff = this.enderstaff.toConfig();
                    config.forcefield = this.forcefield.toConfig();
                    config.magnet = this.magnet.toConfig();
                    config.doublehook = this.doublehook.toConfig();
                    config.rocket = this.rocket.toConfig();
                    return config;
                }

                private static final class Rope {
                    private final DoubleCustomizationValue maxlen;
                    private final BooleanCustomizationValue phaserope;
                    private final BooleanCustomizationValue sticky;

                    private Rope(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.Rope defaults) {
                        builder.comment("Rope customization options.").push("rope");
                        this.maxlen = new DoubleCustomizationValue(builder, "maxlen", defaults.maxlen);
                        this.phaserope = new BooleanCustomizationValue(builder, "phaserope", defaults.phaserope);
                        this.sticky = new BooleanCustomizationValue(builder, "sticky", defaults.sticky);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.Rope toConfig() {
                        Config.GrapplingHook.Custom.Rope config = new Config.GrapplingHook.Custom.Rope();
                        config.maxlen = this.maxlen.toConfig();
                        config.phaserope = this.phaserope.toConfig();
                        config.sticky = this.sticky.toConfig();
                        return config;
                    }
                }

                private static final class HookThrower {
                    private final DoubleCustomizationValue hookgravity;
                    private final DoubleCustomizationValue throwspeed;
                    private final BooleanCustomizationValue reelin;
                    private final DoubleCustomizationValue verticalthrowangle;
                    private final DoubleCustomizationValue sneakingverticalthrowangle;
                    private final BooleanCustomizationValue detachonkeyrelease;

                    private HookThrower(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.HookThrower defaults) {
                        builder.comment("Hook throw and reel behavior.").push("hookthrower");
                        this.hookgravity = new DoubleCustomizationValue(builder, "hookgravity", defaults.hookgravity);
                        this.throwspeed = new DoubleCustomizationValue(builder, "throwspeed", defaults.throwspeed);
                        this.reelin = new BooleanCustomizationValue(builder, "reelin", defaults.reelin);
                        this.verticalthrowangle = new DoubleCustomizationValue(builder, "verticalthrowangle", defaults.verticalthrowangle);
                        this.sneakingverticalthrowangle = new DoubleCustomizationValue(builder, "sneakingverticalthrowangle", defaults.sneakingverticalthrowangle);
                        this.detachonkeyrelease = new BooleanCustomizationValue(builder, "detachonkeyrelease", defaults.detachonkeyrelease);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.HookThrower toConfig() {
                        Config.GrapplingHook.Custom.HookThrower config = new Config.GrapplingHook.Custom.HookThrower();
                        config.hookgravity = this.hookgravity.toConfig();
                        config.throwspeed = this.throwspeed.toConfig();
                        config.reelin = this.reelin.toConfig();
                        config.verticalthrowangle = this.verticalthrowangle.toConfig();
                        config.sneakingverticalthrowangle = this.sneakingverticalthrowangle.toConfig();
                        config.detachonkeyrelease = this.detachonkeyrelease.toConfig();
                        return config;
                    }
                }

                private static final class Motor {
                    private final BooleanCustomizationValue motor;
                    private final DoubleCustomizationValue motormaxspeed;
                    private final DoubleCustomizationValue motoracceleration;
                    private final BooleanCustomizationValue motorwhencrouching;
                    private final BooleanCustomizationValue motorwhennotcrouching;
                    private final BooleanCustomizationValue smartmotor;
                    private final BooleanCustomizationValue motordampener;
                    private final BooleanCustomizationValue pullbackwards;

                    private Motor(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.Motor defaults) {
                        builder.comment("Motor upgrade behavior.").push("motor");
                        this.motor = new BooleanCustomizationValue(builder, "motor", defaults.motor);
                        this.motormaxspeed = new DoubleCustomizationValue(builder, "motormaxspeed", defaults.motormaxspeed);
                        this.motoracceleration = new DoubleCustomizationValue(builder, "motoracceleration", defaults.motoracceleration);
                        this.motorwhencrouching = new BooleanCustomizationValue(builder, "motorwhencrouching", defaults.motorwhencrouching);
                        this.motorwhennotcrouching = new BooleanCustomizationValue(builder, "motorwhennotcrouching", defaults.motorwhennotcrouching);
                        this.smartmotor = new BooleanCustomizationValue(builder, "smartmotor", defaults.smartmotor);
                        this.motordampener = new BooleanCustomizationValue(builder, "motordampener", defaults.motordampener);
                        this.pullbackwards = new BooleanCustomizationValue(builder, "pullbackwards", defaults.pullbackwards);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.Motor toConfig() {
                        Config.GrapplingHook.Custom.Motor config = new Config.GrapplingHook.Custom.Motor();
                        config.motor = this.motor.toConfig();
                        config.motormaxspeed = this.motormaxspeed.toConfig();
                        config.motoracceleration = this.motoracceleration.toConfig();
                        config.motorwhencrouching = this.motorwhencrouching.toConfig();
                        config.motorwhennotcrouching = this.motorwhennotcrouching.toConfig();
                        config.smartmotor = this.smartmotor.toConfig();
                        config.motordampener = this.motordampener.toConfig();
                        config.pullbackwards = this.pullbackwards.toConfig();
                        return config;
                    }
                }

                private static final class Swing {
                    private final DoubleCustomizationValue playermovementmult;

                    private Swing(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.Swing defaults) {
                        builder.comment("Swing movement settings.").push("swing");
                        this.playermovementmult = new DoubleCustomizationValue(builder, "playermovementmult", defaults.playermovementmult);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.Swing toConfig() {
                        Config.GrapplingHook.Custom.Swing config = new Config.GrapplingHook.Custom.Swing();
                        config.playermovementmult = this.playermovementmult.toConfig();
                        return config;
                    }
                }

                private static final class EnderStaff {
                    private final BooleanCustomizationValue enderstaff;

                    private EnderStaff(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.EnderStaff defaults) {
                        builder.comment("Ender staff upgrade option.").push("enderstaff");
                        this.enderstaff = new BooleanCustomizationValue(builder, "enderstaff", defaults.enderstaff);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.EnderStaff toConfig() {
                        Config.GrapplingHook.Custom.EnderStaff config = new Config.GrapplingHook.Custom.EnderStaff();
                        config.enderstaff = this.enderstaff.toConfig();
                        return config;
                    }
                }

                private static final class Forcefield {
                    private final BooleanCustomizationValue repel;
                    private final DoubleCustomizationValue repelforce;

                    private Forcefield(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.Forcefield defaults) {
                        builder.comment("Forcefield upgrade options.").push("forcefield");
                        this.repel = new BooleanCustomizationValue(builder, "repel", defaults.repel);
                        this.repelforce = new DoubleCustomizationValue(builder, "repelforce", defaults.repelforce);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.Forcefield toConfig() {
                        Config.GrapplingHook.Custom.Forcefield config = new Config.GrapplingHook.Custom.Forcefield();
                        config.repel = this.repel.toConfig();
                        config.repelforce = this.repelforce.toConfig();
                        return config;
                    }
                }

                private static final class Magnet {
                    private final BooleanCustomizationValue attract;
                    private final DoubleCustomizationValue attractradius;

                    private Magnet(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.Magnet defaults) {
                        builder.comment("Magnet upgrade options.").push("magnet");
                        this.attract = new BooleanCustomizationValue(builder, "attract", defaults.attract);
                        this.attractradius = new DoubleCustomizationValue(builder, "attractradius", defaults.attractradius);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.Magnet toConfig() {
                        Config.GrapplingHook.Custom.Magnet config = new Config.GrapplingHook.Custom.Magnet();
                        config.attract = this.attract.toConfig();
                        config.attractradius = this.attractradius.toConfig();
                        return config;
                    }
                }

                private static final class DoubleHook {
                    private final BooleanCustomizationValue doublehook;
                    private final BooleanCustomizationValue smartdoublemotor;
                    private final DoubleCustomizationValue angle;
                    private final DoubleCustomizationValue sneakingangle;
                    private final BooleanCustomizationValue oneropepull;

                    private DoubleHook(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.DoubleHook defaults) {
                        builder.comment("Double-hook upgrade options.").push("doublehook");
                        this.doublehook = new BooleanCustomizationValue(builder, "doublehook", defaults.doublehook);
                        this.smartdoublemotor = new BooleanCustomizationValue(builder, "smartdoublemotor", defaults.smartdoublemotor);
                        this.angle = new DoubleCustomizationValue(builder, "angle", defaults.angle);
                        this.sneakingangle = new DoubleCustomizationValue(builder, "sneakingangle", defaults.sneakingangle);
                        this.oneropepull = new BooleanCustomizationValue(builder, "oneropepull", defaults.oneropepull);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.DoubleHook toConfig() {
                        Config.GrapplingHook.Custom.DoubleHook config = new Config.GrapplingHook.Custom.DoubleHook();
                        config.doublehook = this.doublehook.toConfig();
                        config.smartdoublemotor = this.smartdoublemotor.toConfig();
                        config.angle = this.angle.toConfig();
                        config.sneakingangle = this.sneakingangle.toConfig();
                        config.oneropepull = this.oneropepull.toConfig();
                        return config;
                    }
                }

                private static final class Rocket {
                    private final BooleanCustomizationValue rocketenabled;
                    private final DoubleCustomizationValue rocketForce;
                    private final DoubleCustomizationValue rocketActiveTime;
                    private final DoubleCustomizationValue rocketRefuelRatio;
                    private final DoubleCustomizationValue rocketVerticalAngle;

                    private Rocket(ModConfigSpec.Builder builder, Config.GrapplingHook.Custom.Rocket defaults) {
                        builder.comment("Rocket upgrade options.").push("rocket");
                        this.rocketenabled = new BooleanCustomizationValue(builder, "rocketenabled", defaults.rocketenabled);
                        this.rocketForce = new DoubleCustomizationValue(builder, "rocket_force", defaults.rocket_force);
                        this.rocketActiveTime = new DoubleCustomizationValue(builder, "rocket_active_time", defaults.rocket_active_time);
                        this.rocketRefuelRatio = new DoubleCustomizationValue(builder, "rocket_refuel_ratio", defaults.rocket_refuel_ratio);
                        this.rocketVerticalAngle = new DoubleCustomizationValue(builder, "rocket_vertical_angle", defaults.rocket_vertical_angle);
                        builder.pop();
                    }

                    private Config.GrapplingHook.Custom.Rocket toConfig() {
                        Config.GrapplingHook.Custom.Rocket config = new Config.GrapplingHook.Custom.Rocket();
                        config.rocketenabled = this.rocketenabled.toConfig();
                        config.rocket_force = this.rocketForce.toConfig();
                        config.rocket_active_time = this.rocketActiveTime.toConfig();
                        config.rocket_refuel_ratio = this.rocketRefuelRatio.toConfig();
                        config.rocket_vertical_angle = this.rocketVerticalAngle.toConfig();
                        return config;
                    }
                }
            }

            private static final class Blocks {
                private final ModConfigSpec.ConfigValue<String> grapplingBlocks;
                private final ModConfigSpec.ConfigValue<String> grapplingNonBlocks;
                private final ModConfigSpec.ConfigValue<String> grappleBreakBlocks;

                private Blocks(ModConfigSpec.Builder builder, Config.GrapplingHook.Blocks defaults) {
                    builder.comment("Block filtering for grapple attach and break behavior.").push("blocks");
                    this.grapplingBlocks = defineString(builder, "grapplingBlocks", defaults.grapplingBlocks, "Whitelist of blocks the hook can attach to (comma-separated IDs, or 'any').");
                    this.grapplingNonBlocks = defineString(builder, "grapplingNonBlocks", defaults.grapplingNonBlocks, "Blacklist of blocks the hook cannot attach to (comma-separated IDs, or 'none').");
                    this.grappleBreakBlocks = defineString(builder, "grappleBreakBlocks", defaults.grappleBreakBlocks, "Blocks the hook should break when hit (comma-separated IDs, or 'none').");
                    builder.pop();
                }

                private Config.GrapplingHook.Blocks toConfig() {
                    Config.GrapplingHook.Blocks config = new Config.GrapplingHook.Blocks();
                    config.grapplingBlocks = this.grapplingBlocks.get();
                    config.grapplingNonBlocks = this.grapplingNonBlocks.get();
                    config.grappleBreakBlocks = this.grappleBreakBlocks.get();
                    return config;
                }
            }

            private static final class Other {
                private final ModConfigSpec.BooleanValue hookaffectsentities;
                private final ModConfigSpec.DoubleValue ropeSnapBuffer;
                private final ModConfigSpec.IntValue defaultDurability;
                private final ModConfigSpec.DoubleValue ropeJumpPower;
                private final ModConfigSpec.BooleanValue ropeJumpAtAngle;
                private final ModConfigSpec.DoubleValue ropeJumpCooldownS;
                private final ModConfigSpec.DoubleValue climbSpeed;

                private Other(ModConfigSpec.Builder builder, Config.GrapplingHook.Other defaults) {
                    builder.comment("General grappling hook tuning values.").push("other");
                    this.hookaffectsentities = defineBool(builder, "hookaffectsentities", defaults.hookaffectsentities, "If true, hooks can affect entities.");
                    this.ropeSnapBuffer = defineDouble(builder, "rope_snap_buffer", defaults.rope_snap_buffer, -Double.MAX_VALUE, Double.MAX_VALUE, "Extra allowed rope stretch before snapping.");
                    this.defaultDurability = defineInt(builder, "default_durability", defaults.default_durability, 1, Integer.MAX_VALUE, "Default grappling hook durability.");
                    this.ropeJumpPower = defineDouble(builder, "rope_jump_power", defaults.rope_jump_power, -Double.MAX_VALUE, Double.MAX_VALUE, "Maximum jump speed when jumping off rope.");
                    this.ropeJumpAtAngle = defineBool(builder, "rope_jump_at_angle", defaults.rope_jump_at_angle, "Jump in rope direction instead of straight up.");
                    this.ropeJumpCooldownS = defineDouble(builder, "rope_jump_cooldown_s", defaults.rope_jump_cooldown_s, -Double.MAX_VALUE, Double.MAX_VALUE, "Cooldown in seconds between rope jumps.");
                    this.climbSpeed = defineDouble(builder, "climb_speed", defaults.climb_speed, -Double.MAX_VALUE, Double.MAX_VALUE, "Rope climb speed.");
                    builder.pop();
                }

                private Config.GrapplingHook.Other toConfig() {
                    Config.GrapplingHook.Other config = new Config.GrapplingHook.Other();
                    config.hookaffectsentities = this.hookaffectsentities.get();
                    config.rope_snap_buffer = this.ropeSnapBuffer.get();
                    config.default_durability = this.defaultDurability.get();
                    config.rope_jump_power = this.ropeJumpPower.get();
                    config.rope_jump_at_angle = this.ropeJumpAtAngle.get();
                    config.rope_jump_cooldown_s = this.ropeJumpCooldownS.get();
                    config.climb_speed = this.climbSpeed.get();
                    return config;
                }
            }
        }

        private static final class LongFallBoots {
            private final ModConfigSpec.BooleanValue longfallbootsrecipe;

            private LongFallBoots(ModConfigSpec.Builder builder, Config.LongFallBoots defaults) {
                builder.comment("Long fall boots options.").push("longfallboots");
                this.longfallbootsrecipe = defineBool(builder, "longfallbootsrecipe", defaults.longfallbootsrecipe, "Allow crafting long fall boots in the modifier block.");
                builder.pop();
            }

            private Config.LongFallBoots toConfig() {
                Config.LongFallBoots config = new Config.LongFallBoots();
                config.longfallbootsrecipe = this.longfallbootsrecipe.get();
                return config;
            }
        }

        private static final class EnderStaff {
            private final ModConfigSpec.DoubleValue enderStaffStrength;
            private final ModConfigSpec.IntValue enderStaffRecharge;

            private EnderStaff(ModConfigSpec.Builder builder, Config.EnderStaff defaults) {
                builder.comment("Ender staff tuning.").push("enderstaff");
                this.enderStaffStrength = defineDouble(builder, "ender_staff_strength", defaults.ender_staff_strength, -Double.MAX_VALUE, Double.MAX_VALUE, "Launch strength of the ender staff.");
                this.enderStaffRecharge = defineInt(builder, "ender_staff_recharge", defaults.ender_staff_recharge, 0, Integer.MAX_VALUE, "Recharge time for ender staff in ticks.");
                builder.pop();
            }

            private Config.EnderStaff toConfig() {
                Config.EnderStaff config = new Config.EnderStaff();
                config.ender_staff_strength = this.enderStaffStrength.get();
                config.ender_staff_recharge = this.enderStaffRecharge.get();
                return config;
            }
        }

        private static final class Enchantments {
            private final Wallrun wallrun;
            private final DoubleJump doublejump;
            private final Slide slide;

            private Enchantments(ModConfigSpec.Builder builder, Config.Enchantments defaults) {
                builder.comment("Movement enchantment tuning values.").push("enchantments");
                this.wallrun = new Wallrun(builder, defaults.wallrun);
                this.doublejump = new DoubleJump(builder, defaults.doublejump);
                this.slide = new Slide(builder, defaults.slide);
                builder.pop();
            }

            private Config.Enchantments toConfig() {
                Config.Enchantments config = new Config.Enchantments();
                config.wallrun = this.wallrun.toConfig();
                config.doublejump = this.doublejump.toConfig();
                config.slide = this.slide.toConfig();
                return config;
            }

            private static final class Wallrun {
                private final ModConfigSpec.DoubleValue wallJumpUp;
                private final ModConfigSpec.DoubleValue wallJumpSide;
                private final ModConfigSpec.DoubleValue maxWallrunTime;
                private final ModConfigSpec.DoubleValue wallrunSpeed;
                private final ModConfigSpec.DoubleValue wallrunMaxSpeed;
                private final ModConfigSpec.DoubleValue wallrunDrag;
                private final ModConfigSpec.DoubleValue wallrunMinSpeed;

                private Wallrun(ModConfigSpec.Builder builder, Config.Enchantments.Wallrun defaults) {
                    builder.comment("Wallrun enchantment settings.").push("wallrun");
                    this.wallJumpUp = defineDouble(builder, "wall_jump_up", defaults.wall_jump_up, -Double.MAX_VALUE, Double.MAX_VALUE, "Upward speed when jumping off a wall.");
                    this.wallJumpSide = defineDouble(builder, "wall_jump_side", defaults.wall_jump_side, -Double.MAX_VALUE, Double.MAX_VALUE, "Sideways speed when jumping off a wall.");
                    this.maxWallrunTime = defineDouble(builder, "max_wallrun_time", defaults.max_wallrun_time, 0, Double.MAX_VALUE, "Maximum wallrun duration in seconds.");
                    this.wallrunSpeed = defineDouble(builder, "wallrun_speed", defaults.wallrun_speed, -Double.MAX_VALUE, Double.MAX_VALUE, "Wallrun acceleration.");
                    this.wallrunMaxSpeed = defineDouble(builder, "wallrun_max_speed", defaults.wallrun_max_speed, 0, Double.MAX_VALUE, "Maximum wallrun speed.");
                    this.wallrunDrag = defineDouble(builder, "wallrun_drag", defaults.wallrun_drag, -Double.MAX_VALUE, Double.MAX_VALUE, "Wallrun drag.");
                    this.wallrunMinSpeed = defineDouble(builder, "wallrun_min_speed", defaults.wallrun_min_speed, -Double.MAX_VALUE, Double.MAX_VALUE, "Minimum speed required to start wallrunning.");
                    builder.pop();
                }

                private Config.Enchantments.Wallrun toConfig() {
                    Config.Enchantments.Wallrun config = new Config.Enchantments.Wallrun();
                    config.wall_jump_up = this.wallJumpUp.get();
                    config.wall_jump_side = this.wallJumpSide.get();
                    config.max_wallrun_time = this.maxWallrunTime.get();
                    config.wallrun_speed = this.wallrunSpeed.get();
                    config.wallrun_max_speed = this.wallrunMaxSpeed.get();
                    config.wallrun_drag = this.wallrunDrag.get();
                    config.wallrun_min_speed = this.wallrunMinSpeed.get();
                    return config;
                }
            }

            private static final class DoubleJump {
                private final ModConfigSpec.DoubleValue doublejumpforce;
                private final ModConfigSpec.BooleanValue relativeToFalling;
                private final ModConfigSpec.DoubleValue dontDoubleJumpIfFallingFasterThan;

                private DoubleJump(ModConfigSpec.Builder builder, Config.Enchantments.DoubleJump defaults) {
                    builder.comment("Double-jump enchantment settings.").push("doublejump");
                    this.doublejumpforce = defineDouble(builder, "doublejumpforce", defaults.doublejumpforce, -Double.MAX_VALUE, Double.MAX_VALUE, "Vertical boost applied by double jump.");
                    this.relativeToFalling = defineBool(builder, "doublejump_relative_to_falling", defaults.doublejump_relative_to_falling, "Keep vertical momentum when double jumping.");
                    this.dontDoubleJumpIfFallingFasterThan = defineDouble(builder, "dont_doublejump_if_falling_faster_than", defaults.dont_doublejump_if_falling_faster_than, -Double.MAX_VALUE, Double.MAX_VALUE, "Block double jump if falling faster than this speed.");
                    builder.pop();
                }

                private Config.Enchantments.DoubleJump toConfig() {
                    Config.Enchantments.DoubleJump config = new Config.Enchantments.DoubleJump();
                    config.doublejumpforce = this.doublejumpforce.get();
                    config.doublejump_relative_to_falling = this.relativeToFalling.get();
                    config.dont_doublejump_if_falling_faster_than = this.dontDoubleJumpIfFallingFasterThan.get();
                    return config;
                }
            }

            private static final class Slide {
                private final ModConfigSpec.DoubleValue slidingjumpforce;
                private final ModConfigSpec.DoubleValue slidingFriction;
                private final ModConfigSpec.DoubleValue slidingMinSpeed;
                private final ModConfigSpec.DoubleValue slidingEndMinSpeed;

                private Slide(ModConfigSpec.Builder builder, Config.Enchantments.Slide defaults) {
                    builder.comment("Sliding enchantment settings.").push("slide");
                    this.slidingjumpforce = defineDouble(builder, "slidingjumpforce", defaults.slidingjumpforce, -Double.MAX_VALUE, Double.MAX_VALUE, "Vertical boost when jumping from a slide.");
                    this.slidingFriction = defineDouble(builder, "sliding_friction", defaults.sliding_friction, -Double.MAX_VALUE, Double.MAX_VALUE, "Sliding friction.");
                    this.slidingMinSpeed = defineDouble(builder, "sliding_min_speed", defaults.sliding_min_speed, -Double.MAX_VALUE, Double.MAX_VALUE, "Minimum speed to start sliding.");
                    this.slidingEndMinSpeed = defineDouble(builder, "sliding_end_min_speed", defaults.sliding_end_min_speed, -Double.MAX_VALUE, Double.MAX_VALUE, "Minimum speed to keep sliding.");
                    builder.pop();
                }

                private Config.Enchantments.Slide toConfig() {
                    Config.Enchantments.Slide config = new Config.Enchantments.Slide();
                    config.slidingjumpforce = this.slidingjumpforce.get();
                    config.sliding_friction = this.slidingFriction.get();
                    config.sliding_min_speed = this.slidingMinSpeed.get();
                    config.sliding_end_min_speed = this.slidingEndMinSpeed.get();
                    return config;
                }
            }
        }

        private static final class Other {
            private final ModConfigSpec.BooleanValue overrideAllowFlight;
            private final ModConfigSpec.DoubleValue airstrafeMaxSpeed;
            private final ModConfigSpec.DoubleValue airstrafeAcceleration;
            private final ModConfigSpec.BooleanValue dontOverrideMovementInAir;

            private Other(ModConfigSpec.Builder builder, Config.Other defaults) {
                builder.comment("Miscellaneous server-side grappling settings.").push("other");
                this.overrideAllowFlight = defineBool(builder, "override_allowflight", defaults.override_allowflight, "Automatically enable allow-flight on the server.");
                this.airstrafeMaxSpeed = defineDouble(builder, "airstrafe_max_speed", defaults.airstrafe_max_speed, -Double.MAX_VALUE, Double.MAX_VALUE, "Maximum mid-air movement speed while grapple control is active.");
                this.airstrafeAcceleration = defineDouble(builder, "airstrafe_acceleration", defaults.airstrafe_acceleration, -Double.MAX_VALUE, Double.MAX_VALUE, "Mid-air movement acceleration while grapple control is active.");
                this.dontOverrideMovementInAir = defineBool(builder, "dont_override_movement_in_air", defaults.dont_override_movement_in_air, "Disable grapple movement override while in air.");
                builder.pop();
            }

            private Config.Other toConfig() {
                Config.Other config = new Config.Other();
                config.override_allowflight = this.overrideAllowFlight.get();
                config.airstrafe_max_speed = this.airstrafeMaxSpeed.get();
                config.airstrafe_acceleration = this.airstrafeAcceleration.get();
                config.dont_override_movement_in_air = this.dontOverrideMovementInAir.get();
                return config;
            }
        }
    }

    public static final class Client {
        private final Camera camera;
        private final Sounds sounds;

        private Client(ModConfigSpec.Builder builder) {
            ClientConfig defaults = new ClientConfig();
            this.camera = new Camera(builder, defaults.camera);
            this.sounds = new Sounds(builder, defaults.sounds);
        }

        private ClientConfig toConfig() {
            ClientConfig config = new ClientConfig();
            config.camera = this.camera.toConfig();
            config.sounds = this.sounds.toConfig();
            return config;
        }

        private static final class Camera {
            private final ModConfigSpec.DoubleValue wallrunCameraTiltDegrees;
            private final ModConfigSpec.DoubleValue wallrunCameraAnimationS;

            private Camera(ModConfigSpec.Builder builder, ClientConfig.Camera defaults) {
                builder.comment("Client-side camera effects.").push("camera");
                this.wallrunCameraTiltDegrees = defineDouble(builder, "wallrun_camera_tilt_degrees", defaults.wallrun_camera_tilt_degrees, -Double.MAX_VALUE, Double.MAX_VALUE, "Camera roll angle while wallrunning.");
                this.wallrunCameraAnimationS = defineDouble(builder, "wallrun_camera_animation_s", defaults.wallrun_camera_animation_s, 0, Double.MAX_VALUE, "Duration for wallrun camera tilt animation in seconds.");
                builder.pop();
            }

            private ClientConfig.Camera toConfig() {
                ClientConfig.Camera config = new ClientConfig.Camera();
                config.wallrun_camera_tilt_degrees = this.wallrunCameraTiltDegrees.get().floatValue();
                config.wallrun_camera_animation_s = this.wallrunCameraAnimationS.get().floatValue();
                return config;
            }
        }

        private static final class Sounds {
            private final ModConfigSpec.DoubleValue wallrunSoundEffectTimeS;
            private final ModConfigSpec.DoubleValue wallrunSoundVolume;
            private final ModConfigSpec.DoubleValue doublejumpSoundVolume;
            private final ModConfigSpec.DoubleValue slideSoundVolume;
            private final ModConfigSpec.DoubleValue wallrunjumpSoundVolume;
            private final ModConfigSpec.DoubleValue rocketSoundVolume;
            private final ModConfigSpec.DoubleValue enderstaffSoundVolume;

            private Sounds(ModConfigSpec.Builder builder, ClientConfig.Sounds defaults) {
                builder.comment("Client-side sound volume controls.").push("sounds");
                this.wallrunSoundEffectTimeS = defineDouble(builder, "wallrun_sound_effect_time_s", defaults.wallrun_sound_effect_time_s, 0, Double.MAX_VALUE, "Minimum interval between wallrun footstep sounds in seconds.");
                this.wallrunSoundVolume = defineDouble(builder, "wallrun_sound_volume", defaults.wallrun_sound_volume, 0, Double.MAX_VALUE, "Wallrun footstep volume.");
                this.doublejumpSoundVolume = defineDouble(builder, "doublejump_sound_volume", defaults.doublejump_sound_volume, 0, Double.MAX_VALUE, "Double-jump sound volume.");
                this.slideSoundVolume = defineDouble(builder, "slide_sound_volume", defaults.slide_sound_volume, 0, Double.MAX_VALUE, "Slide sound volume.");
                this.wallrunjumpSoundVolume = defineDouble(builder, "wallrunjump_sound_volume", defaults.wallrunjump_sound_volume, 0, Double.MAX_VALUE, "Wallrun-jump sound volume.");
                this.rocketSoundVolume = defineDouble(builder, "rocket_sound_volume", defaults.rocket_sound_volume, 0, Double.MAX_VALUE, "Rocket sound volume.");
                this.enderstaffSoundVolume = defineDouble(builder, "enderstaff_sound_volume", defaults.enderstaff_sound_volume, 0, Double.MAX_VALUE, "Ender staff sound volume.");
                builder.pop();
            }

            private ClientConfig.Sounds toConfig() {
                ClientConfig.Sounds config = new ClientConfig.Sounds();
                config.wallrun_sound_effect_time_s = this.wallrunSoundEffectTimeS.get();
                config.wallrun_sound_volume = this.wallrunSoundVolume.get().floatValue();
                config.doublejump_sound_volume = this.doublejumpSoundVolume.get().floatValue();
                config.slide_sound_volume = this.slideSoundVolume.get().floatValue();
                config.wallrunjump_sound_volume = this.wallrunjumpSoundVolume.get().floatValue();
                config.rocket_sound_volume = this.rocketSoundVolume.get().floatValue();
                config.enderstaff_sound_volume = this.enderstaffSoundVolume.get().floatValue();
                return config;
            }
        }
    }

    public static class Config {
        public GrapplingHook grapplinghook = new GrapplingHook();

        public static class GrapplingHook {
            public Custom custom = new Custom();

            public static class Custom {
                public static class DoubleCustomizationOption {
                    public double default_value;
                    public int enabled;
                    public double max;
                    public double max_upgraded;
                    public double min;
                    public double min_upgraded;

                    public DoubleCustomizationOption(double default_value, int enabled, double max, double max_upgraded) {
                        this.default_value = default_value;
                        this.enabled = enabled;
                        this.max = max;
                        this.max_upgraded = max_upgraded;
                        this.min = 0;
                        this.min_upgraded = 0;
                    }

                    public DoubleCustomizationOption(double default_value, int enabled, double max, double max_upgraded, double min, double min_upgraded) {
                        this(default_value, enabled, max, max_upgraded);
                        this.min = min;
                        this.min_upgraded = min_upgraded;
                    }
                }

                public static class BooleanCustomizationOption {
                    public boolean default_value;
                    public int enabled;

                    public BooleanCustomizationOption(boolean default_value, int enabled) {
                        this.default_value = default_value;
                        this.enabled = enabled;
                    }
                }

                public Rope rope = new Rope();

                public static class Rope {
                    public DoubleCustomizationOption maxlen = new DoubleCustomizationOption(30, 0, 60, 200);
                    public BooleanCustomizationOption phaserope = new BooleanCustomizationOption(false, 0);
                    public BooleanCustomizationOption sticky = new BooleanCustomizationOption(false, 0);
                }

                public HookThrower hookthrower = new HookThrower();

                public static class HookThrower {
                    public DoubleCustomizationOption hookgravity = new DoubleCustomizationOption(1F, 0, 100, 100, 1, 0);
                    public DoubleCustomizationOption throwspeed = new DoubleCustomizationOption(2F, 0, 5, 20);
                    public BooleanCustomizationOption reelin = new BooleanCustomizationOption(true, 0);
                    public DoubleCustomizationOption verticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
                    public DoubleCustomizationOption sneakingverticalthrowangle = new DoubleCustomizationOption(0F, 0, 45, 90);
                    public BooleanCustomizationOption detachonkeyrelease = new BooleanCustomizationOption(false, 0);
                }

                public Motor motor = new Motor();

                public static class Motor {
                    public BooleanCustomizationOption motor = new BooleanCustomizationOption(false, 0);
                    public DoubleCustomizationOption motormaxspeed = new DoubleCustomizationOption(4, 0, 4, 10);
                    public DoubleCustomizationOption motoracceleration = new DoubleCustomizationOption(0.2, 0, 0.2, 1);
                    public BooleanCustomizationOption motorwhencrouching = new BooleanCustomizationOption(false, 0);
                    public BooleanCustomizationOption motorwhennotcrouching = new BooleanCustomizationOption(true, 0);
                    public BooleanCustomizationOption smartmotor = new BooleanCustomizationOption(false, 0);
                    public BooleanCustomizationOption motordampener = new BooleanCustomizationOption(false, 1);
                    public BooleanCustomizationOption pullbackwards = new BooleanCustomizationOption(true, 0);
                }

                public Swing swing = new Swing();

                public static class Swing {
                    public DoubleCustomizationOption playermovementmult = new DoubleCustomizationOption(1, 0, 2, 5);
                }

                public EnderStaff enderstaff = new EnderStaff();

                public static class EnderStaff {
                    public BooleanCustomizationOption enderstaff = new BooleanCustomizationOption(false, 0);
                }

                public Forcefield forcefield = new Forcefield();

                public static class Forcefield {
                    public BooleanCustomizationOption repel = new BooleanCustomizationOption(false, 0);
                    public DoubleCustomizationOption repelforce = new DoubleCustomizationOption(1, 0, 1, 5);
                }

                public Magnet magnet = new Magnet();

                public static class Magnet {
                    public BooleanCustomizationOption attract = new BooleanCustomizationOption(false, 0);
                    public DoubleCustomizationOption attractradius = new DoubleCustomizationOption(3, 0, 3, 10);
                }

                public DoubleHook doublehook = new DoubleHook();

                public static class DoubleHook {
                    public BooleanCustomizationOption doublehook = new BooleanCustomizationOption(false, 0);
                    public BooleanCustomizationOption smartdoublemotor = new BooleanCustomizationOption(true, 0);
                    public DoubleCustomizationOption angle = new DoubleCustomizationOption(20, 0, 45, 90);
                    public DoubleCustomizationOption sneakingangle = new DoubleCustomizationOption(10, 0, 45, 90);
                    public BooleanCustomizationOption oneropepull = new BooleanCustomizationOption(false, 0);
                }

                public Rocket rocket = new Rocket();

                public static class Rocket {
                    public BooleanCustomizationOption rocketenabled = new BooleanCustomizationOption(false, 0);
                    public DoubleCustomizationOption rocket_force = new DoubleCustomizationOption(1, 0, 1, 5);
                    public DoubleCustomizationOption rocket_active_time = new DoubleCustomizationOption(0.5, 0, 0.5, 20);
                    public DoubleCustomizationOption rocket_refuel_ratio = new DoubleCustomizationOption(15, 0, 30, 30, 15, 1);
                    public DoubleCustomizationOption rocket_vertical_angle = new DoubleCustomizationOption(0, 0, 90, 90);
                }
            }

            public Blocks blocks = new Blocks();

            public static class Blocks {
                public String grapplingBlocks = "any";
                public String grapplingNonBlocks = "none";
                public String grappleBreakBlocks = "none";
            }

            public Other other = new Other();

            public static class Other {
                public boolean hookaffectsentities = true;
                public double rope_snap_buffer = 5;
                public int default_durability = 500;
                public double rope_jump_power = 1;
                public boolean rope_jump_at_angle = false;
                public double rope_jump_cooldown_s = 0;
                public double climb_speed = 0.3;
            }
        }

        public LongFallBoots longfallboots = new LongFallBoots();

        public static class LongFallBoots {
            public boolean longfallbootsrecipe = true;
        }

        public EnderStaff enderstaff = new EnderStaff();

        public static class EnderStaff {
            public double ender_staff_strength = 1.5;
            public int ender_staff_recharge = 100;
        }

        public Enchantments enchantments = new Enchantments();

        public static class Enchantments {
            public Wallrun wallrun = new Wallrun();

            public static class Wallrun {
                public double wall_jump_up = 0.7;
                public double wall_jump_side = 0.4;
                public double max_wallrun_time = 3;
                public double wallrun_speed = 0.1;
                public double wallrun_max_speed = 0.7;
                public double wallrun_drag = 0.01;
                public double wallrun_min_speed = 0;
            }

            public DoubleJump doublejump = new DoubleJump();

            public static class DoubleJump {
                public double doublejumpforce = 0.8;
                public boolean doublejump_relative_to_falling = false;
                public double dont_doublejump_if_falling_faster_than = 99999999.0;
            }

            public Slide slide = new Slide();

            public static class Slide {
                public double slidingjumpforce = 0.6;
                public double sliding_friction = 1 / 150F;
                public double sliding_min_speed = 0.15;
                public double sliding_end_min_speed = 0.01;
            }
        }

        public Other other = new Other();

        public static class Other {
            public boolean override_allowflight = true;
            public double airstrafe_max_speed = 0.7;
            public double airstrafe_acceleration = 0.015;
            public boolean dont_override_movement_in_air = false;
        }
    }

    public static class ClientConfig {
        public Camera camera = new Camera();

        public static class Camera {
            public float wallrun_camera_tilt_degrees = 10;
            public float wallrun_camera_animation_s = 0.5f;
        }

        public Sounds sounds = new Sounds();

        public static class Sounds {
            public double wallrun_sound_effect_time_s = 0.35;
            public float wallrun_sound_volume = 1.0F;
            public float doublejump_sound_volume = 1.0F;
            public float slide_sound_volume = 1.0F;
            public float wallrunjump_sound_volume = 1.0F;
            public float rocket_sound_volume = 1.0F;
            public float enderstaff_sound_volume = 1.0F;
        }
    }
}
