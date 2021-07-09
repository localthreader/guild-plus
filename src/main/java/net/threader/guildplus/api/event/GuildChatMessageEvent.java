package net.threader.guildplus.api.event;

import net.threader.guildplus.model.Member;
import net.threader.guildplus.utils.message.Message;
import org.bukkit.event.Cancellable;

public class GuildChatMessageEvent extends GuildEvent implements Cancellable {
    private Message message;
    private Member sender;
    private boolean canceled;

    public GuildChatMessageEvent(Message message, Member sender) {
        super(sender.getGuild());
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
