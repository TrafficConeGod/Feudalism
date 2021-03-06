package feudalism.object;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.Printable;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.azrael.Readable;
import feudalism.Config;
import feudalism.FeudalismException;
import feudalism.Registry;
import feudalism.Util;

/*
average towny resident: weakguy.png
average feudalism user: strongguy.png
*/
public class User implements Printable, Readable {
    private UUID uuid;
    private List<Realm> ownedRealms = new ArrayList<>();
    private boolean hasMemberRealm = false;
    private Realm memberRealm;

    public User() throws FeudalismException {
        uuid = UUID.randomUUID();
        // if (!Util.isValidPlayerUuid(uuid)) {
        //     throw new FeudalismException(String.format("%s is not a valid player uuid", uuid.toString()));
        // }
        Registry.getInstance().addUser(this);
    }

    public User(UUID uuid) throws FeudalismException {
        if (!Util.isValidPlayerUuid(uuid)) {
            throw new FeudalismException(String.format("%s is not a valid player uuid", uuid.toString()));
        }
        this.uuid = uuid;
        Registry.getInstance().addUser(this);
    }

    private static float getUpkeepFactor() {
        return Config.getFloat("realm.upkeep_factor");
    }

    public static User get(UUID uuid) throws FeudalismException {
        try {
			return Registry.getInstance().getUserByUuid(uuid);
		} catch (FeudalismException e) {
			return new User(uuid);
		}
    }

    public static User get(Player player) throws FeudalismException {
        return get(player.getUniqueId());
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void transferMoney(User to, float amount) {
        Registry.getInstance().getEconomy().withdrawPlayer(getOfflinePlayer(), amount);
        Registry.getInstance().getEconomy().depositPlayer(getOfflinePlayer(), amount);
    }

    public void removeMoney(float amount) {
        Registry.getInstance().getEconomy().withdrawPlayer(getOfflinePlayer(), amount);
    }

    public boolean hasMoney(float amount) {
        return Registry.getInstance().getEconomy().has(getOfflinePlayer(), amount);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    public List<Realm> getOwnedRealms() {
        return ownedRealms;
    }

    public boolean ownsRealm(Realm realm) {
        return ownedRealms.contains(realm);
    }

    public void realmOnlyAddRealm(Realm realm) {
        ownedRealms.add(realm);
    }

    public void realmOnlyRemoveRealm(Realm realm) {
        ownedRealms.remove(realm);
    }

    public int getClaims() {
        int claims = 0;
        for (Realm realm : ownedRealms) {
            claims += realm.getClaims().size();
        }
        return claims;
    }

    public float getUpkeep() {
        int claims = getClaims();
        return (claims * claims) * getUpkeepFactor();
    }
    
    public boolean hasPerm(Realm realm, PermType type) {
        return realm.hasPerm(this, type);
    }

    public boolean hasMemberRealm() {
        return hasMemberRealm;
    }

    public void realmOnlySetMemberRealm(Realm memberRealm) throws FeudalismException {
        if (hasMemberRealm) {
            throw new FeudalismException("Already has member realm");
        }
        hasMemberRealm = true;
        this.memberRealm = memberRealm;
    }

    public void realmOnlyRemoveMemberRealm() {
        hasMemberRealm = false;
    }

    public Realm getMemberRealm() throws FeudalismException {
        if (!hasMemberRealm()) {
            throw new FeudalismException("User does not have a member realm");
        }
        return memberRealm;
    }

    public void checkOnline() throws FeudalismException {
        OfflinePlayer offlinePlayer = getOfflinePlayer();
        if (!offlinePlayer.isOnline()) {
            throw new FeudalismException(String.format("%s must be online to do this", offlinePlayer.getName()));
        }
    }

    @Override
    public Object print(ObjectPrinter<?> printer) throws PrintException {
        List<Object> list = new ArrayList<>();
        list.add(uuid.toString());
        return printer.print(list);
    }

    @Override
    public Object read(ObjectReader<?> reader, Object object) throws ReadException {
        try {
            List<Object> list = (ArrayList<Object>) reader.read(object);
            User user = new User(UUID.fromString((String) list.get(0)));
            return user;
        } catch (FeudalismException e) {
            throw new ReadException(e);
        }
    }
}
