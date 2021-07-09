package net.threader.guildplus.api.event;

import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.model.Member;
import org.bukkit.event.Cancellable;

public class ClanChatMessageEvent extends ClanEvent implements Cancellable {
    private Message message;
    private Member sender;
    private boolean canceled;

    public ClanChatMessageEvent(Message message, Member sender) {
        super(sender.getClan());
        this.message = message;
        this.sender = sender;
    }

    public Message getMessage() {
        return message;
    }

    public Member getSender() {
        return sender;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }
    
}
