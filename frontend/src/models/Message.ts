import type { ItemPreviewType } from '@/models/Item.ts'

export interface MessageUser {
  id: number | string;
  username: string;
  displayName?: string;
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
  isRead: boolean;
  content?: string;
  sentDate: Date;
}

export interface ConversationPreview {
  id: number | string;
  otherUser: MessageUser;
  item: ItemPreviewType;
  lastMessage: Message | null;
  unreadMessagesCount: number;
  relatedItem?: {
    id: number | string;
    title: string;
  }
}
