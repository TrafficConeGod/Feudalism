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
import feudalism.FeudalismException;
import feudalism.Registry;
import feudalism.Util;

/*
average towny resident: weakguy.png
average feudalism user: strongguy.png
*/
public class User implements Printable, Readable {
    private UUID uuid;

    public User() {

    }

    public User(UUID uuid) throws FeudalismException {
        if (!Util.isValidPlayerUuid(uuid)) {
            throw new FeudalismException(String.format("%s is not a valid player uuid", uuid.toString()));
        }
        this.uuid = uuid;
        Registry.getInstance().addUser(this);
    }

    public static User get(UUID uuid) throws FeudalismException {
        try {
			return Registry.getInstance().getUserByUuid(uuid);
		} catch (FeudalismException e) {
			return new User(uuid);
		}
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

    @Override
    public String toString() {
        return uuid.toString();
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
