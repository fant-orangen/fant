export interface MessageUser {
  id: number | string;
  username: string;
}

export interface Message {
  id: number | string;
  sender: MessageUser;
  receiver: MessageUser;
  item?: {
    id: number | string;
    title: string;
  }
  messageContent: string;
  sentDate: Date;
}

export interface ConversationPreview {
  id: number | string;
  otherUser: MessageUser;
  lastMessage: Message | null; // Preview snippet of last message
  unreadMessagesCount: number;
  relatedItem?: {
    id: number | string;
    title: string;
  }
}
