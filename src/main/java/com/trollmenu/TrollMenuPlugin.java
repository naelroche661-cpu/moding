package com.trollmenu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrollMenuPlugin extends JavaPlugin implements Listener {

    private static final NamespacedKey MENU_KEY = new NamespacedKey("trollmenu", "menu_item");
    private static final String MENU_TITLE = "§8§l▎ §6§lTrollMenu §8§l▎";
    private final Map<UUID, Boolean> frozenPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> vanishedPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> godPlayers = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        registerCommands();
        getLogger().info("§aTrollMenuPlugin activé ! §7/ §etrollmenu §7pour ouvrir.");
    }

    private void registerCommands() {
        getCommand("trollmenu").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player p)) {
                sender.sendMessage("§cJoueurs seulement.");
                return true;
            }
            if (!p.hasPermission("trollmenu.use")) {
                p.sendMessage("§cPas la permission.");
                return true;
            }
            openMainMenu(p);
            return true;
        });
    }

    private void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(new MenuHolder(), 36, MENU_TITLE);
        inv.setItem(0, createItem(Material.PACKED_ICE, "§b❄ §lFreeze / Unfreeze", List.of("§7Cible: regarde un joueur §8→ §eclique", "§8▸ §eGauche §7: Freeze", "§8▸ §eDroit §7: Unfreeze"), "freeze"));
        inv.setItem(1, createItem(Material.GRAY_DYE, "§8§l▸ §7Vanish (Invisible)", List.of("§8▸ §eClic §7: Toggle vanish"), "vanish"));
        inv.setItem(2, createItem(Material.GRASS_BLOCK, "§a§l▸ §2Survival", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "gms"));
        inv.setItem(3, createItem(Material.DIAMOND, "§b§l▸ §3Créatif", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "gmc"));
        inv.setItem(4, createItem(Material.MAP, "§6§l▸ §eAventure", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "gma"));
        inv.setItem(5, createItem(Material.ENDER_EYE, "§5§l▸ §dSpectateur", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "gmsp"));
        inv.setItem(6, createItem(Material.GOLDEN_APPLE, "§c❤ §lSoigner", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "heal"));
        inv.setItem(7, createItem(Material.COOKED_BEEF, "§6🍖 §lNourrir", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "feed"));
        inv.setItem(8, createItem(Material.FEATHER, "§b✈ §lFly", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "fly"));
        inv.setItem(9, createItem(Material.NETHER_STAR, "§c☠ §lGodmode", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "god"));
        inv.setItem(10, createItem(Material.BARRIER, "§c§l▸ §4Fake Kick", List.of("§7Regarde un joueur §8→ §eclique"), "fakekick"));
        inv.setItem(11, createItem(Material.REDSTONE_BLOCK, "§4§l▸ §cFake Ban", List.of("§7Regarde un joueur §8→ §eclique"), "fakeban"));
        inv.setItem(12, createItem(Material.COMMAND_BLOCK, "§6§l▸ §eFake OP", List.of("§7Regarde un joueur §8→ §eclique"), "fakeop"));
        inv.setItem(13, createItem(Material.LIGHTNING_ROD, "§e⚡ §lFoudre sur cible", List.of("§7Regarde un joueur §8→ §eclique"), "lightning"));
        inv.setItem(14, createItem(Material.TNT, "§c💥 §lExplosion sur cible", List.of("§7Regarde un joueur §8→ §eclique"), "explosion"));
        inv.setItem(15, createItem(Material.DIAMOND, "§b💎 §lDiamonds x64", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "give_dia"));
        inv.setItem(16, createItem(Material.NETHERITE_INGOT, "§8§l▸ §7Netherite x64", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "give_netherite"));
        inv.setItem(17, createItem(Material.EXPERIENCE_BOTTLE, "§3✨ §lXP 100 niveaux", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "give_xp"));
        inv.setItem(18, createItem(Material.SUNFLOWER, "§e☀ §lJour", List.of("§8▸ §eClic §7: Temps = jour"), "time_day"));
        inv.setItem(19, createItem(Material.DARK_OAK_SAPLING, "§8☾ §lNuit", List.of("§8▸ §eClic §7: Temps = nuit"), "time_night"));
        inv.setItem(20, createItem(Material.BLUE_DYE, "§b☀ §lMétéo: Clair", List.of("§8▸ §eClic §7: Arrête pluie/orage"), "weather_clear"));
        inv.setItem(21, createItem(Material.LIGHT_BLUE_DYE, "§9🌧 §lMétéo: Orage", List.of("§8▸ §eClic §7: Déclenche orage"), "weather_storm"));
        inv.setItem(22, createItem(Material.ENDER_PEARL, "§d➤ §lTP vers cible", List.of("§7Regarde un joueur §8→ §eclique"), "tp_to"));
        inv.setItem(23, createItem(Material.COMPASS, "§e➤ §lTP cible ici", List.of("§7Regarde un joueur §8→ §eclique"), "tp_here"));
        inv.setItem(24, createItem(Material.BED, "§a➤ §lSpawn", List.of("§8▸ §eGauche/Droit §7: Cible regardée"), "spawn"));
        inv.setItem(25, createItem(Material.IRON_DOOR, "§c§l▸ §4Vrai Kick", List.of("§7Regarde un joueur §8→ §eclique"), "kick_real"));
        inv.setItem(26, createItem(Material.BEDROCK, "§4§l▸ §cVrai Ban", List.of("§7Regarde un joueur §8→ §eclique"), "ban_real"));
        inv.setItem(35, createItem(Material.BARRIER, "§c§l▸ §4Fermer", List.of("§8▸ §eClic §7: Fermer"), "close"));
        p.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, List<String> lore, String actionId) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        List<Component> loreComp = new ArrayList<>();
        for (String line : lore) {
            loreComp.add(Component.text(line));
        }
        meta.lore(loreComp);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, actionId);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!e.getView().title().equals(Component.text(MENU_TITLE))) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        String action = item.getItemMeta().getPersistentDataContainer().get(MENU_KEY, PersistentDataType.STRING);
        if (action == null) return;
        Player target = getTargetedPlayer(p);
        if (target == null && needsTarget(action)) {
            p.sendMessage("§cRegarde un joueur pour cibler.");
            return;
        }
        executeAction(p, target, action);
        if (!action.equals("close")) p.closeInventory();
    }

    private boolean needsTarget(String action) {
        return switch (action) {
            case "freeze", "gms", "gmc", "gma", "gmsp", "heal", "feed", "fly", "god",
                 "fakekick", "fakeban", "fakeop", "lightning", "explosion",
                 "give_dia", "give_netherite", "give_xp", "tp_to", "tp_here", "spawn",
                 "kick_real", "ban_real" -> true;
            default -> false;
        };
    }

    private Player getTargetedPlayer(Player p) {
        var ray = p.raycastEntities(6.0).filter(e -> e instanceof Player).limit(1).findFirst();
        return ray.map(e -> (Player) e).orElse(null);
    }

    private void executeAction(Player p, Player target, String action) {
        switch (action) {
            case "freeze" -> toggleFreeze(target);
            case "vanish" -> toggleVanish(p);
            case "gms" -> setGamemode(target, GameMode.SURVIVAL);
            case "gmc" -> setGamemode(target, GameMode.CREATIVE);
            case "gma" -> setGamemode(target, GameMode.ADVENTURE);
            case "gmsp" -> setGamemode(target, GameMode.SPECTATOR);
            case "heal" -> heal(target);
            case "feed" -> feed(target);
            case "fly" -> toggleFly(target);
            case "god" -> toggleGod(target);
            case "fakekick" -> fakeKick(target);
            case "fakeban" -> fakeBan(target);
            case "fakeop" -> fakeOp(target);
            case "lightning" -> strikeLightning(target);
            case "explosion" -> explode(target);
            case "give_dia" -> giveItem(target, new ItemStack(Material.DIAMOND, 64));
            case "give_netherite" -> giveItem(target, new ItemStack(Material.NETHERITE_INGOT, 64));
            case "give_xp" -> giveXp(target, 100);
            case "time_day" -> setTime(p, 1000);
            case "time_night" -> setTime(p, 13000);
            case "weather_clear" -> setWeather(p, false);
            case "weather_storm" -> setWeather(p, true);
            case "tp_to" -> p.teleport(target);
            case "tp_here" -> target.teleport(p);
            case "spawn" -> target.teleport(target.getWorld().getSpawnLocation());
            case "kick_real" -> kickReal(target);
            case "ban_real" -> banReal(target);
            case "close" -> {}
        }
    }

    private void toggleFreeze(Player target) {
        UUID id = target.getUniqueId();
        boolean frozen = frozenPlayers.compute(id, (k, v) -> v == null || !v);
        target.setWalkSpeed(frozen ? 0f : 0.2f);
        target.sendMessage(frozen ? "§cTu es gelé !" : "§aTu n'es plus gelé.");
    }

    private void toggleVanish(Player p) {
        UUID id = p.getUniqueId();
        boolean vanished = vanishedPlayers.compute(id, (k, v) -> v == null || !v);
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (vanished) online.hidePlayer(this, p);
            else online.showPlayer(this, p);
        }
        p.sendMessage(vanished ? "§aVanish activé." : "§cVanish désactivé.");
    }

    private void setGamemode(Player target, GameMode gm) {
        target.setGameMode(gm);
        target.sendMessage("§aGamemode changé: §e" + gm.name());
    }

    private void heal(Player target) {
        target.setHealth(target.getAttribute(Attribute.MAX_HEALTH).getValue());
        target.setFireTicks(0);
        target.removePotionEffect(PotionEffectType.POISON);
        target.removePotionEffect(PotionEffectType.WITHER);
        target.sendMessage("§aSoigné !");
    }

    private void feed(Player target) {
        target.setFoodLevel(20);
        target.setSaturation(20f);
        target.sendMessage("§aNourri !");
    }

    private void toggleFly(Player target) {
        boolean fly = !target.getAllowFlight();
        target.setAllowFlight(fly);
        target.setFlying(fly);
        target.sendMessage(fly ? "§aFly activé." : "§cFly désactivé.");
    }

    private void toggleGod(Player target) {
        UUID id = target.getUniqueId();
        boolean god = godPlayers.compute(id, (k, v) -> v == null || !v);
        target.setInvulnerable(god);
        target.sendMessage(god ? "§aGodmode activé." : "§cGodmode désactivé.");
    }

    private void fakeKick(Player target) {
        String msg = "§c[AntiCheat] §fCheat détecté: §cFly §8| §fPing: §c999ms";
        Bukkit.broadcast(Component.text("§e" + target.getName() + " §7a été déconnecté: " + msg));
    }

    private void fakeBan(Player target) {
        Bukkit.broadcast(Component.text("§4§l[BAN] §c" + target.getName() + " §7a été banni définitivement: §fHack client détecté (KillAura, Reach, Fly)"));
    }

    private void fakeOp(Player target) {
        Bukkit.broadcast(Component.text("§6§l[SYSTEM] §e" + target.getName() + " §7vient d'obtenir les permissions §6OP §7par §eConsole"));
    }

    private void strikeLightning(Player target) {
        target.getWorld().strikeLightning(target.getLocation());
    }

    private void explode(Player target) {
        target.getWorld().createExplosion(target.getLocation(), 3f, false, true);
    }

    private void giveItem(Player target, ItemStack item) {
        target.getInventory().addItem(item);
        target.sendMessage("§aReçu: §e" + item.getAmount() + "x " + item.getType().name());
    }

    private void giveXp(Player target, int levels) {
        target.addExpLevels(levels);
        target.sendMessage("§aXP ajouté: §e" + levels + " niveaux");
    }

    private void setTime(Player p, long time) {
        p.getWorld().setTime(time);
        p.sendMessage("§aTemps changé.");
    }

    private void setWeather(Player p, boolean storm) {
        p.getWorld().setStorm(storm);
        p.getWorld().setThundering(storm);
        p.sendMessage("§aMétéo changée: " + (storm ? "§9Orage" : "§bClair"));
    }

    private void kickReal(Player target) {
        target.kick(Component.text("§cKick par l'admin"));
    }

    private void banReal(Player target) {
        String name = target.getName();
        String reason = "Banni par l'admin";
        Bukkit.getBanList(BanList.Type.NAME).addBan(name, reason, null, "Admin");
        target.kick(Component.text("§cBanni par l'admin"));
    }

    private static class MenuHolder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }
}
