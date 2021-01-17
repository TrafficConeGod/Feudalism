package feudalism.command;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import feudalism.Chat;
import feudalism.Config;
import feudalism.FeudalismException;
import feudalism.Registry;
import feudalism.Util;
import feudalism.object.Confirmation;
import feudalism.object.GridCoord;
import feudalism.object.PermType;
import feudalism.object.Perms;
import feudalism.object.Realm;
import feudalism.object.Request;
import feudalism.object.User;

public class SelectCommands {
    public static final CommandElement[] elements = new CommandElement[] {
        new Claim(),
        new Set(),
        new Abandon(),
        new ViewPerms(),
        new Invite()
    };
    
    private static class Abandon implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "abandon" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 0;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            Chat.sendMessage(sender, "Are you sure you want to abandon this realm?");
            new Confirmation(sender, () -> {
                Realm realm = (Realm) data.get(0);
                realm.removeOwner();
                Chat.sendMessage(sender, String.format("Successfully abandoned realm %s", realm.getName()));
            });
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }

    private static class Claim implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "claim" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 0;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            Realm realm = (Realm) data.get(0);
            Player player = (Player) sender;
            User user = User.get(player);
            GridCoord coord = GridCoord.getFromLocation(player.getLocation());
            if (coord.hasOwner()) {
                throw new FeudalismException("You can not claim owned land");
            }
            if (!realm.hasDirectBorder(coord)) {
                throw new FeudalismException("You can not claim land that you do not border");
            }
            float claimPrice = Config.getFloat("realm.claim_price");
            if (!user.hasMoney(claimPrice)) {
                throw new FeudalismException(String.format("You need at least %s in your account to claim", claimPrice));
            }
            user.removeMoney(claimPrice);
            realm.addClaim(coord);
            Chat.sendMessage(sender, String.format("Successfully claimed %s, %s for %s", coord.getGridX(), coord.getGridZ(), claimPrice));
            coord.clean();
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }
    
    private static class Set implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "set" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 1;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[] {
                new Owner(),
                new SetPerm()
            };
        }

        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            CommandElement element = getSubelementWithAlias(args[0]);
            element.execute(sender, alias, Util.trimArgs(args, 1), data);
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            if (args.length == 1) {
                return getSubaliases();
            }
            CommandElement element = getSubelementWithAlias(args[0]);
            return element.getTabComplete(sender, alias, Util.trimArgs(args, 1), data);
        }

        private class Owner implements CommandElement {

            @Override
            public String[] getAliases() {
                return new String[] { "owner" };
            }
        
            @Override
            public int getRequiredArgs() {
                return 1;
            }
        
            @Override
            public CommandElement[] getSubelements() {
                return new CommandElement[0];
            }
        
            @Override
            public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
                Realm realm = (Realm) data.get(0);
                User user = Util.getUserByNameAndCheckOnline(args[0]);
                Player player = (Player) sender;
                Player newOwnerPlayer = user.getPlayer();
                float personalUnionFormPrice = Config.getFloat("realm.personal_union_form_price");
                if (!user.hasMoney(personalUnionFormPrice)) {
                    throw new FeudalismException(String.format("%s needs at least %s in their account to form a personal union", newOwnerPlayer.getDisplayName(), personalUnionFormPrice));
                }
                Chat.sendMessage(sender, String.format("Are you sure you want to change ownership of %s to %s?", realm.getName(), newOwnerPlayer.getDisplayName()));
                new Confirmation(player, () -> {
                    Chat.sendMessage(newOwnerPlayer, String.format("%s is offering you to become the new owner of %s. Do you accept? This action will cost %s.", player.getDisplayName(), realm.getName(), personalUnionFormPrice));
                    new Request(newOwnerPlayer, () -> {
                        realm.setOwner(user);
                        user.removeMoney(personalUnionFormPrice);
                        Chat.sendMessage(sender, String.format("%s accepted your request to change ownership of %s", newOwnerPlayer.getDisplayName(), realm.getName()));
                        Chat.sendMessage(newOwnerPlayer, String.format("Successfully gained ownership of %s", realm.getName()));
                    }, () -> {
                        Chat.sendMessage(sender, String.format("%s denied your request to be the new owner", newOwnerPlayer.getDisplayName()));
                    });
                });
            }
        
            @Override
            public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
                return Registry.getInstance().getPlayerNames();
            }
            
        }

        private class SetPerm implements CommandElement {

            @Override
            public String[] getAliases() {
                return new String[] { "perm" };
            }
        
            @Override
            public int getRequiredArgs() {
                return 3;
            }
        
            @Override
            public CommandElement[] getSubelements() {
                return new CommandElement[0];
            }
        
            @Override
            public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
                Realm realm = (Realm) data.get(0);
                String category = args[0];
                String typeString = args[1];
                String statusString = args[2];
                PermType type = Registry.getInstance().getPermTypeByDisplayName(typeString);
                if (!statusString.equals("enabled") && !statusString.equals("disabled")) {
                    throw new FeudalismException("Status must be enabled or disabled");
                }
                boolean status = statusString.equals("enabled") ? true : false;
                switch (category) {
                    case "outsiders":
                        realm.getOutsiderPerms().set(type, status);
                        break;
                    case "members":
                        realm.getMemberPerms().set(type, status);
                        break;
                    case "subjects":
                        realm.getSubjectPerms().set(type, status);
                        break;
                    case "overlords":
                        realm.getOverlordPerms().set(type, status);
                        break;
                    default:
                        throw new FeudalismException("Category must be: outsiders, members, subjects, or overlords");
                }
                Chat.sendMessage(sender, String.format("Successfully set perm %s on category %s to %s", category, typeString, statusString));
            }
        
            @Override
            public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
                if (args.length == 1) {
                    List<String> categories = new ArrayList<>();
                    categories.add("outsiders");
                    categories.add("members");
                    categories.add("subjects");
                    categories.add("overlords");
                    return categories;
                }
                if (args.length == 2) {
                    List<String> types = new ArrayList<>();
                    for (PermType type : Registry.getInstance().getPermTypes()) {
                        types.add(type.getDisplayName());
                    }
                    return types;
                }
                if (args.length == 3) {
                    List<String> statuses = new ArrayList<>();
                    statuses.add("enabled");
                    statuses.add("disabled");
                    return statuses;
                }
                return new ArrayList<>();
            }
            
        }
        
    }

    private static class ViewPerms implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "perms" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 0;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[0];
        }

        private String getPermsString(Perms perms) {
            String output = "";
            for (PermType type : Registry.getInstance().getPermTypes()) {
                boolean status = perms.get(type);
                output += String.format("%s: %s, ", type.getDisplayName(), status ? "enabled" : "disabled");
            }
            return output;
        }
    
        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            Realm realm = (Realm) data.get(0);
            Chat.sendStatMessage(sender, "Outsiders", getPermsString(realm.getOutsiderPerms()));
            Chat.sendStatMessage(sender, "Members", getPermsString(realm.getMemberPerms()));
            Chat.sendStatMessage(sender, "Subjects", getPermsString(realm.getSubjectPerms()));
            Chat.sendStatMessage(sender, "Overlords", getPermsString(realm.getOverlordPerms()));
        }
    
        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            return new ArrayList<>();
        }
        
    }

    private static class Invite implements CommandElement {

        @Override
        public String[] getAliases() {
            return new String[] { "invite" };
        }
    
        @Override
        public int getRequiredArgs() {
            return 1;
        }
    
        @Override
        public CommandElement[] getSubelements() {
            return new CommandElement[] {
                new InviteMember()
            };
        }

        @Override
        public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            CommandElement element = getSubelementWithAlias(args[0]);
            element.execute(sender, alias, Util.trimArgs(args, 1), data);
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
            if (args.length == 1) {
                return getSubaliases();
            }
            CommandElement element = getSubelementWithAlias(args[0]);
            return element.getTabComplete(sender, alias, Util.trimArgs(args, 1), data);
        }
        
        private class InviteMember implements CommandElement {

            @Override
            public String[] getAliases() {
                return new String[] { "member" };
            }
        
            @Override
            public int getRequiredArgs() {
                return 1;
            }
        
            @Override
            public CommandElement[] getSubelements() {
                return new CommandElement[0];
            }
        
            @Override
            public void onExecute(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
                Realm realm = (Realm) data.get(0);
                User user = Util.getUserByNameAndCheckOnline(args[0]);
                if (user.hasMemberRealm() || user.getOwnedRealms().size() > 0) {
                    throw new FeudalismException(String.format("%s must not be a member of a realm or own any realms", args[0]));
                }
                Player sPlayer = (Player) sender;
                Chat.sendMessage(sender, String.format("Sent invite request to %s", args[0]));
                Chat.sendMessage(user.getPlayer(), String.format("%s is offering you to become a member of %s. Do you accept?", sPlayer.getDisplayName(), realm.getName()));
                new Request(user.getPlayer(), () -> {
                    realm.addMember(user);
                    Chat.sendMessage(sender, String.format("%s accepted your request to join %s", args[0], realm.getName()));
                    Chat.sendMessage(user.getPlayer(), String.format("Successfully became member of %s", realm.getName()));
                }, () -> {
                    Chat.sendMessage(sender, String.format("%s denied your request to join %s", realm.getName()));
                });
            }
        
            @Override
            public List<String> onTabComplete(CommandSender sender, String alias, String[] args, List<Object> data) throws FeudalismException {
                return Registry.getInstance().getPlayerNames();
            }
            
        }

    }
}
