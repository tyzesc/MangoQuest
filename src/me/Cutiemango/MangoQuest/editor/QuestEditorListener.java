package me.Cutiemango.MangoQuest.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.Questi18n;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.RequirementType;
import net.citizensnpcs.api.npc.NPC;

public class QuestEditorListener
{

	public static HashMap<String, String> CurrentListening = new HashMap<>();

	public static void onChat(final Player p, final String msg, Cancellable event)
	{
		if (CurrentListening.containsKey(p.getName()))
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					p.performCommand(CurrentListening.get(p.getName()) + msg);
					CurrentListening.remove(p.getName());
				}
			}.runTaskAsynchronously(Main.instance);
			if (msg.contains("cancel"))
				QuestChatManager.info(p, Questi18n.localizeMessage("EditorMessage.CancelEntry"));
			else
				QuestChatManager.info(p, Questi18n.localizeMessage("EditorMessage.YourEntry", msg));
			event.setCancelled(true);
		}
		else
			return;
	}

	public static void onPlayerInteract(Player p, Action act, ItemStack is)
	{
		if (act.equals(Action.RIGHT_CLICK_AIR) || act.equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (is != null && !is.getType().equals(Material.AIR))
			{
				if (CurrentListening.containsKey(p.getName())
						&& (CurrentListening.get(p.getName()).contains("item") || CurrentListening.get(p.getName()).contains("ITEM")))
				{
					p.performCommand(CurrentListening.get(p.getName()) + "hand");
					CurrentListening.remove(p.getName());
				}
				else
					return;
			}
		}
		return;
	}

	@SuppressWarnings("deprecation")
	public static void onBlockBreak(Player p, Block b, Cancellable event)
	{
		if (p.isOp() && QuestEditorManager.isInEditorMode(p))
		{
			if (CurrentListening.containsKey(p.getName()) && CurrentListening.get(p.getName()).contains("block"))
			{
				event.setCancelled(true);
				p.performCommand(CurrentListening.get(p.getName()) + b.getType().toString() + ":" + b.getData());
				CurrentListening.remove(p.getName());
			}
			else
				return;
		}
	}

	public static void onEntityDamage(Player p, Entity e, Cancellable event)
	{
		if (QuestEditorManager.isInEditorMode(p) && CurrentListening.containsKey(p.getName()))
		{
			event.setCancelled(true);
			if (CurrentListening.get(p.getName()).contains("mtmmob"))
				if (Main.instance.initManager.hasMythicMobEnabled() && Main.instance.initManager.getMythicMobsAPI().isMythicMob(e))
					p.performCommand(CurrentListening.get(p.getName())
							+ Main.instance.initManager.getMythicMobsAPI().getMythicMobInstance(e).getType().getInternalName());
				else
					p.performCommand(CurrentListening.get(p.getName()) + e.getType().toString());
			else
				if (CurrentListening.get(p.getName()).contains("mobname"))
					if (e.getCustomName() != null)
						p.performCommand(CurrentListening.get(p.getName()) + e.getCustomName());
					else
						p.performCommand(CurrentListening.get(p.getName()) + QuestUtil.translate(e.getType()));
				else
					if (CurrentListening.get(p.getName()).contains("mobtype"))
						p.performCommand(CurrentListening.get(p.getName()) + e.getType().toString());
			CurrentListening.remove(p.getName());
		}
	}

	public static void onNPCLeftClick(Player p, NPC npc, Cancellable event)
	{
		if (CurrentListening.containsKey(p.getName()))
		{
			p.performCommand(CurrentListening.get(p.getName()) + npc.getId());
			QuestChatManager.info(p, Questi18n.localizeMessage("EditorMessage.NPCSelcted", npc.getName()));
			CurrentListening.remove(p.getName());
			event.setCancelled(true);
			return;
		}
	}

	public static void onInventoryClose(Player p, Inventory inv)
	{
		if (CurrentListening.containsKey(p.getName()) && QuestEditorManager.isInEditorMode(p))
		{
			if (!CurrentListening.get(p.getName()).contains("inv"))
				return;
			Quest q = QuestEditorManager.getCurrentEditingQuest(p);
			List<ItemStack> list = new ArrayList<>();
			for (ItemStack is : inv.getContents())
			{
				if (is == null || is.getType().equals(Material.AIR))
					continue;
				else
					list.add(is);
			}
			if (inv.getName().contains("Reward"))
			{
				q.getQuestReward().setItemReward(list);
				QuestEditorManager.editQuest(p);
			}
			else
				if (inv.getName().contains("Requirement"))
				{
					q.getRequirements().put(RequirementType.ITEM, list);
					QuestEditorManager.editQuestRequirement(p);
				}
			QuestChatManager.info(p, Questi18n.localizeMessage("EditorMessage.ItemSaved"));
			CurrentListening.remove(p.getName());
			return;
		}
	}

	public static void registerListeningObject(Player p, String cmd)
	{
		CurrentListening.put(p.getName(), cmd);
	}

	@SuppressWarnings("unchecked")
	public static void registerGUI(Player p, String obj)
	{
		if (QuestEditorManager.isInEditorMode(p))
		{
			if (obj.equalsIgnoreCase("reward"))
				QuestEditorManager.generateEditItemGUI(p, "Reward", QuestEditorManager.getCurrentEditingQuest(p).getQuestReward().getItems());
			else
				if (obj.equalsIgnoreCase("requirement"))
					QuestEditorManager.generateEditItemGUI(p, "Requirement",
							(List<ItemStack>) QuestEditorManager.getCurrentEditingQuest(p).getRequirements().get(RequirementType.ITEM));
				else
					return;
			CurrentListening.put(p.getName(), "inv");
		}
	}

}
