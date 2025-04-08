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

export interface PaginatedMessageResponse {
  content: Message[];        // The current page of messages
  totalPages: number;        // Total number of pages available
  totalElements: number;     // Total number of messages
  size: number;              // Number of messages per page
  number: number;            // Current page index (0-based)
  first: boolean;            // Whether this is the first page
  last: boolean;             // Whether this is the last page
  empty: boolean;            // Whether the current page has no content
}
