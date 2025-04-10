/**
 * @fileoverview Message model definitions for user communications.
 * <p>This module provides types for:</p>
 * <ul>
 *   <li>Private messaging between users</li>
 *   <li>Item-related conversations</li>
 *   <li>Conversation management and previews</li>
 *   <li>Pagination support for message history</li>
 * </ul>
 */

import type { ItemPreviewType } from '@/models/Item.ts'

/**
 * Represents a user in the messaging system.
 * Contains essential user information for message display.
 * @interface MessageUser
 */
export interface MessageUser {
  /**
   * The unique identifier for the user.
   * @type {number|string}
   */
  id: number | string;

  /**
   * The unique username of the user.
   * Used for identification in the system.
   * @type {string}
   */
  username: string;

  /**
   * Optional display name that may be shown instead of username.
   * Provides a more personalized presentation in the UI.
   * @type {string|undefined}
   */
  displayName?: string;
}

/**
 * Represents a single message in the messaging system.
 * Contains the content and metadata about the communication.
 * @interface Message
 */
export interface Message {
  /**
   * The unique identifier for the message.
   * @type {number|string}
   */
  id: number | string;

  /**
   * The user who sent the message.
   * @type {MessageUser}
   */
  sender: MessageUser;

  /**
   * The user receiving the message.
   * @type {MessageUser}
   */
  receiver: MessageUser;

  /**
   * Optional item reference when the message is about a specific listing.
   * @type {{id: number|string, title: string}|undefined}
   */
  item?: {
    id: number | string;
    title: string;
  }

  /**
   * The text content of the message.
   * @type {string}
   */
  messageContent: string;

  /**
   * Flag indicating whether the message has been read by the recipient.
   * @type {boolean}
   */
  isRead: boolean;

  /**
   * Optional additional content or legacy content field.
   * @type {string|undefined}
   */
  content?: string;

  /**
   * The date and time when the message was sent.
   * @type {Date}
   */
  sentDate: Date;
}

/**
 * Represents a preview of a conversation between users.
 * Used in conversation listings and inbox views.
 * @interface ConversationPreview
 */
export interface ConversationPreview {
  /**
   * The unique identifier for the conversation.
   * @type {number|string}
   */
  id: number | string;

  /**
   * The user on the other end of the conversation (not the current user).
   * @type {MessageUser}
   */
  otherUser: MessageUser;

  /**
   * The item related to this conversation.
   * @type {ItemPreviewType}
   */
  item: ItemPreviewType;

  /**
   * The most recent message in the conversation, if any.
   * @type {Message|null}
   */
  lastMessage: Message | null;

  /**
   * Count of unread messages in this conversation.
   * Used for notification badges in the UI.
   * @type {number}
   */
  unreadMessagesCount: number;

  /**
   * Optional item reference in alternative format.
   * @type {{id: number|string, title: string}|undefined}
   */
  relatedItem?: {
    id: number | string;
    title: string;
  }
}

/**
 * Represents a paginated response of messages from the API.
 * Used for conversation history views.
 * @interface PaginatedMessageResponse
 */
export interface PaginatedMessageResponse {
  /**
   * The array of message objects for the current page.
   * @type {Message[]}
   */
  content: Message[];

  /**
   * Total number of pages available.
   * @type {number}
   */
  totalPages: number;

  /**
   * Total number of messages across all pages.
   * @type {number}
   */
  totalElements: number;

  /**
   * Number of messages per page.
   * @type {number}
   */
  size: number;

  /**
   * Current page number (0-based).
   * @type {number}
   */
  number: number;

  /**
   * Whether this is the first page.
   * @type {boolean}
   */
  first: boolean;

  /**
   * Whether this is the last page.
   * @type {boolean}
   */
  last: boolean;

  /**
   * Whether the current page is empty.
   * @type {boolean}
   */
  empty: boolean;
}
