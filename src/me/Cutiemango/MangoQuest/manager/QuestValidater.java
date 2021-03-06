package me.Cutiemango.MangoQuest.manager;

import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.Cutiemango.MangoQuest.DebugHandler;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.conversation.FriendConversation;
import me.Cutiemango.MangoQuest.conversation.QuestConversation;
import me.Cutiemango.MangoQuest.conversation.StartTriggerConversation;
import me.Cutiemango.MangoQuest.model.Quest;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public class QuestValidater
{
	
	public static boolean isWorld(String s)
	{
		return s != null && Bukkit.getWorld(s) != null;
	}
	
	public static boolean weakItemCheck(ItemStack original, ItemStack compare)
	{
		if (original.getType().equals(compare.getType()))
		{
			if (original.hasItemMeta() && compare.hasItemMeta())
			{
				ItemMeta im = original.getItemMeta(), mm = compare.getItemMeta();

				// Optional.equals(): The other object is considered equal if:
				// it is also an Optional,
				// and both instances have no value present,
				// or the present values are "equal to" each other via equals().
				Optional<String> oriName = Optional.ofNullable(im.getDisplayName()), cmpName = Optional.ofNullable(mm.getDisplayName());
				// Name check
				if (!oriName.equals(cmpName))
				{
					DebugHandler.log(5, "[ItemCheck] Item displayName mismatch.");
					return false;
				}

				// List.equals() returns true if and only if:
				// The specified object is also a list,
				// both lists have the same size,
				// and all corresponding pairs of elements in the two lists are equal.
				Optional<List<String>> oriLore = Optional.ofNullable(im.getLore()), cmpLore = Optional.ofNullable(mm.getLore());

				// Lore check
				if (!oriLore.equals(cmpLore))
				{
					DebugHandler.log(5, "[ItemCheck] Item lore mismatch.");
					return false;
				}

				return true;
			}
			DebugHandler.log(5, "[ItemCheck] ItemMeta mismatch.");
			return false;
		}
		DebugHandler.log(5, "[ItemCheck] Type is not correct.");
		return false;
	}
	
	public static boolean validateMythicMob(String id)
	{
		if (!Main.getHooker().hasMythicMobEnabled())
			return false;
		MythicMob m = Main.getHooker().getMythicMob(id);
		return m != null;
	}

	public static boolean validateNPC(String id)
	{
		if (!validateInteger(id))
			return false;
		NPC npc = Main.getHooker().getNPC(id);
		return npc != null;
	}

	public static boolean validateInteger(String number)
	{
		try
		{
			Integer.parseInt(number);
		}
		catch (NumberFormatException | NullPointerException e)
		{
			return false;
		}
		return true;
	}
	
	public static boolean detailedValidate(Quest before, Quest after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		if (!(before.isCommandQuest() == after.isCommandQuest()))
			return false;
		if (!before.isCommandQuest())
		{
			if (!(before.getQuestNPC().getId() == after.getQuestNPC().getId()))
				return false;
		}
		if (!before.getQuestOutline().equals(after.getQuestOutline()))
			return false;
		if (!before.getFailMessage().equals(after.getFailMessage()))
			return false;
		if (!before.getQuestReward().equals(after.getQuestReward()))
			return false;
		if (!(before.isRedoable() == after.isRedoable()))
			return false;
		if (before.isRedoable())
		{
			if (!(before.getRedoDelay() == after.getRedoDelay()))
				return false;
		}
		if (!before.getRequirements().equals(after.getRequirements()))
			return false;
		if (!before.getStages().equals(after.getStages()))
			return false;
		if (!before.getTriggerMap().equals(after.getTriggerMap()))
			return false;
		return true;
	}
	
	public static boolean detailedValidate(QuestConversation before, QuestConversation after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getName().equals(after.getName()))
			return false;
		if (!(before.getNPC().getId() == after.getNPC().getId()))
			return false;
		if (!before.getActions().equals(after.getActions()))
			return false;
		if (!(before instanceof FriendConversation && after instanceof FriendConversation))
			return false;
		if (before instanceof FriendConversation && after instanceof FriendConversation)
			if (!(((FriendConversation)before).getReqPoint() == ((FriendConversation)after).getReqPoint()))
				return false;
		if (!(before instanceof StartTriggerConversation && after instanceof StartTriggerConversation))
			return false;
		if (before instanceof StartTriggerConversation && after instanceof StartTriggerConversation)
			if (!((StartTriggerConversation)before).getQuest().equals(((StartTriggerConversation)after).getQuest()))
				return false;
		return true;
	}

	public static boolean weakValidate(Quest before, Quest after)
	{
		if (before == null || after == null)
			return false;
		if (!before.getInternalID().equals(after.getInternalID()))
			return false;
		if (!before.getQuestName().equals(after.getQuestName()))
			return false;
		return true;
	}
}
