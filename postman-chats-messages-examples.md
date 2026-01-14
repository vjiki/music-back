# Postman Examples for Chats and Messages API

## Base URL
```
http://localhost:8080
```
(Adjust port if your application runs on a different port)

---

## Chat Endpoints

### 1. Get All Chats for a User

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/chats/user/123e4567-e89b-12d3-a456-426614174000`

**Expected Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "type": "DIRECT",
    "title": null,
    "lastMessage": {
      "id": "789e4567-e89b-12d3-a456-426614174111",
      "content": "Hey, how are you?",
      "senderId": "456e7890-e89b-12d3-a456-426614174222",
      "createdAt": "2026-01-03T10:30:00Z"
    },
    "participants": [
      {
        "userId": "123e4567-e89b-12d3-a456-426614174000",
        "userNickname": "john_doe",
        "userAvatarUrl": "https://example.com/avatar.jpg"
      },
      {
        "userId": "456e7890-e89b-12d3-a456-426614174222",
        "userNickname": "jane_smith",
        "userAvatarUrl": null
      }
    ],
    "unreadCount": 2,
    "updatedAt": "2026-01-03T10:30:00Z"
  }
]
```

---

### 2. Get Chat by ID

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/chats/550e8400-e29b-41d4-a716-446655440000`

**Expected Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "type": "DIRECT",
  "title": null,
  "description": null,
  "avatarUrl": null,
  "ownerId": null,
  "ownerNickname": null,
  "isEncrypted": false,
  "isArchived": false,
  "isMuted": false,
  "createdAt": "2026-01-03T10:00:00Z",
  "updatedAt": "2026-01-03T10:30:00Z",
  "participants": [
    {
      "userId": "123e4567-e89b-12d3-a456-426614174000",
      "userEmail": "john@example.com",
      "userNickname": "john_doe",
      "userAvatarUrl": "https://example.com/avatar.jpg",
      "role": "MEMBER",
      "joinedAt": "2026-01-03T10:00:00Z",
      "isMuted": false
    }
  ]
}
```

---

### 3. Create a Direct Chat

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/chats`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "type": "DIRECT",
    "participantIds": [
      "123e4567-e89b-12d3-a456-426614174000",
      "456e7890-e89b-12d3-a456-426614174222"
    ],
    "isEncrypted": false
  }
  ```

**Expected Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "type": "DIRECT",
  "title": null,
  "description": null,
  "avatarUrl": null,
  "ownerId": null,
  "ownerNickname": null,
  "isEncrypted": false,
  "isArchived": false,
  "isMuted": false,
  "createdAt": "2026-01-03T10:00:00Z",
  "updatedAt": "2026-01-03T10:00:00Z",
  "participants": [
    {
      "userId": "123e4567-e89b-12d3-a456-426614174000",
      "userEmail": "john@example.com",
      "userNickname": "john_doe",
      "userAvatarUrl": "https://example.com/avatar.jpg",
      "role": "MEMBER",
      "joinedAt": "2026-01-03T10:00:00Z",
      "isMuted": false
    },
    {
      "userId": "456e7890-e89b-12d3-a456-426614174222",
      "userEmail": "jane@example.com",
      "userNickname": "jane_smith",
      "userAvatarUrl": null,
      "role": "MEMBER",
      "joinedAt": "2026-01-03T10:00:00Z",
      "isMuted": false
    }
  ]
}
```

---

### 4. Create a Group Chat

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/chats`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "type": "GROUP",
    "title": "Music Lovers",
    "description": "A group for music enthusiasts",
    "avatarUrl": "https://example.com/group-avatar.jpg",
    "ownerId": "123e4567-e89b-12d3-a456-426614174000",
    "participantIds": [
      "123e4567-e89b-12d3-a456-426614174000",
      "456e7890-e89b-12d3-a456-426614174222",
      "789e1234-e89b-12d3-a456-426614174333"
    ],
    "isEncrypted": false
  }
  ```

**Expected Response (200 OK):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "type": "GROUP",
  "title": "Music Lovers",
  "description": "A group for music enthusiasts",
  "avatarUrl": "https://example.com/group-avatar.jpg",
  "ownerId": "123e4567-e89b-12d3-a456-426614174000",
  "ownerNickname": "john_doe",
  "isEncrypted": false,
  "isArchived": false,
  "isMuted": false,
  "createdAt": "2026-01-03T10:00:00Z",
  "updatedAt": "2026-01-03T10:00:00Z",
  "participants": [
    {
      "userId": "123e4567-e89b-12d3-a456-426614174000",
      "userEmail": "john@example.com",
      "userNickname": "john_doe",
      "userAvatarUrl": "https://example.com/avatar.jpg",
      "role": "OWNER",
      "joinedAt": "2026-01-03T10:00:00Z",
      "isMuted": false
    },
    {
      "userId": "456e7890-e89b-12d3-a456-426614174222",
      "userEmail": "jane@example.com",
      "userNickname": "jane_smith",
      "userAvatarUrl": null,
      "role": "MEMBER",
      "joinedAt": "2026-01-03T10:00:00Z",
      "isMuted": false
    }
  ]
}
```

---

### 5. Delete a Chat

**Request:**
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/chats/550e8400-e29b-41d4-a716-446655440000`

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

**Note:** This performs a hard delete. All participants and messages will be cascade deleted.

---

## Message Endpoints

### 6. Get Messages in Chat (Non-Paginated)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/messages/chat/550e8400-e29b-41d4-a716-446655440000?userId1=123e4567-e89b-12d3-a456-426614174000&userId2=456e7890-e89b-12d3-a456-426614174222`

**Expected Response (200 OK):**
```json
[
  {
    "id": "789e4567-e89b-12d3-a456-426614174111",
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "123e4567-e89b-12d3-a456-426614174000",
    "senderEmail": "john@example.com",
    "senderNickname": "john_doe",
    "senderAvatarUrl": "https://example.com/avatar.jpg",
    "replyToId": null,
    "messageType": "TEXT",
    "content": "Hey, how are you?",
    "songId": null,
    "attachmentCount": 0,
    "isEdited": false,
    "isDeleted": false,
    "createdAt": "2026-01-03T10:30:00Z",
    "updatedAt": "2026-01-03T10:30:00Z"
  },
  {
    "id": "999e4567-e89b-12d3-a456-426614174222",
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "456e7890-e89b-12d3-a456-426614174222",
    "senderEmail": "jane@example.com",
    "senderNickname": "jane_smith",
    "senderAvatarUrl": null,
    "replyToId": "789e4567-e89b-12d3-a456-426614174111",
    "messageType": "TEXT",
    "content": "I'm doing great, thanks!",
    "songId": null,
    "attachmentCount": 0,
    "isEdited": false,
    "isDeleted": false,
    "createdAt": "2026-01-03T10:31:00Z",
    "updatedAt": "2026-01-03T10:31:00Z"
  }
]
```

---

### 7. Get Messages in Chat (Paginated - First Page)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/messages/chat/550e8400-e29b-41d4-a716-446655440000/page?limit=20`

**Expected Response (200 OK):**
```json
{
  "items": [
    {
      "id": "789e4567-e89b-12d3-a456-426614174111",
      "chatId": "550e8400-e29b-41d4-a716-446655440000",
      "senderId": "123e4567-e89b-12d3-a456-426614174000",
      "senderEmail": "john@example.com",
      "senderNickname": "john_doe",
      "senderAvatarUrl": "https://example.com/avatar.jpg",
      "replyToId": null,
      "messageType": "TEXT",
      "content": "Hey, how are you?",
      "songId": null,
      "attachmentCount": 0,
      "isEdited": false,
      "isDeleted": false,
      "createdAt": "2026-01-03T10:30:00Z",
      "updatedAt": "2026-01-03T10:30:00Z"
    }
  ],
  "nextCursor": "eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTAzVDEwOjMwOjAwWiIsImlkIjoiNzg5ZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MTExIn0",
  "hasNext": true
}
```

---

### 8. Get Messages in Chat (Paginated - Next Page)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/messages/chat/550e8400-e29b-41d4-a716-446655440000/page?limit=20&cursor=eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTAzVDEwOjMwOjAwWiIsImlkIjoiNzg5ZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MTExIn0`

**Expected Response (200 OK):**
```json
{
  "items": [
    {
      "id": "888e4567-e89b-12d3-a456-426614174333",
      "chatId": "550e8400-e29b-41d4-a716-446655440000",
      "senderId": "456e7890-e89b-12d3-a456-426614174222",
      "senderEmail": "jane@example.com",
      "senderNickname": "jane_smith",
      "senderAvatarUrl": null,
      "replyToId": null,
      "messageType": "TEXT",
      "content": "Earlier message",
      "songId": null,
      "attachmentCount": 0,
      "isEdited": false,
      "isDeleted": false,
      "createdAt": "2026-01-03T10:25:00Z",
      "updatedAt": "2026-01-03T10:25:00Z"
    }
  ],
  "nextCursor": null,
  "hasNext": false
}
```

---

### 9. Create a Text Message

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/messages`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "123e4567-e89b-12d3-a456-426614174000",
    "content": "Hey, how are you?",
    "messageType": "TEXT"
  }
  ```

**Expected Response (200 OK):**
```json
{
  "id": "789e4567-e89b-12d3-a456-426614174111",
  "chatId": "550e8400-e29b-41d4-a716-446655440000",
  "senderId": "123e4567-e89b-12d3-a456-426614174000",
  "senderEmail": "john@example.com",
  "senderNickname": "john_doe",
  "senderAvatarUrl": "https://example.com/avatar.jpg",
  "replyToId": null,
  "messageType": "TEXT",
  "content": "Hey, how are you?",
  "songId": null,
  "attachmentCount": 0,
  "isEdited": false,
  "isDeleted": false,
  "createdAt": "2026-01-03T10:30:00Z",
  "updatedAt": "2026-01-03T10:30:00Z"
}
```

---

### 10. Create a Reply Message

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/messages`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "456e7890-e89b-12d3-a456-426614174222",
    "content": "I'm doing great, thanks!",
    "replyToId": "789e4567-e89b-12d3-a456-426614174111",
    "messageType": "TEXT"
  }
  ```

**Expected Response (200 OK):**
Same format as above, but with `replyToId` set to the parent message ID.

---

### 11. Create a Message with Song

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/messages`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "chatId": "550e8400-e29b-41d4-a716-446655440000",
    "senderId": "123e4567-e89b-12d3-a456-426614174000",
    "content": "Check out this song!",
    "messageType": "SONG",
    "songId": "6cf4bd03-cf16-4682-b867-ff038f5fe4d9",
    "attachmentCount": 0
  }
  ```

**Expected Response (200 OK):**
Same format as above, but with `messageType` set to "SONG" and `songId` populated.

---

### 12. Delete a Message

**Request:**
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/messages/789e4567-e89b-12d3-a456-426614174111`

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

**Note:** This performs a soft delete. The message will be marked as deleted but not removed from the database.

---

### 13. Get Message Reactions

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/messages/789e4567-e89b-12d3-a456-426614174111/reactions`

**Expected Response (200 OK):**
```json
[
  {
    "messageId": "789e4567-e89b-12d3-a456-426614174111",
    "userId": "456e7890-e89b-12d3-a456-426614174222",
    "emoji": "👍",
    "created_at": "2026-01-03T10:35:00Z"
  },
  {
    "messageId": "789e4567-e89b-12d3-a456-426614174111",
    "userId": "789e1234-e89b-12d3-a456-426614174333",
    "emoji": "❤️",
    "created_at": "2026-01-03T10:36:00Z"
  }
]
```

---

### 14. Add Reaction to Message

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/messages/reactions`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "messageId": "789e4567-e89b-12d3-a456-426614174111",
    "userId": "456e7890-e89b-12d3-a456-426614174222",
    "emoji": "👍"
  }
  ```

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

---

### 15. Remove Reaction from Message

**Request:**
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/messages/789e4567-e89b-12d3-a456-426614174111/reactions/456e7890-e89b-12d3-a456-426614174222/👍`

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

**Note:** The emoji in the URL should be URL-encoded. For example, "👍" should be encoded as "%F0%9F%91%8D"

---

### 16. Mark Message as Read

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/messages/789e4567-e89b-12d3-a456-426614174111/read/456e7890-e89b-12d3-a456-426614174222`

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

**Note:** This will also update the participant's `last_read_message_id` in the chat.

---

## Error Cases

### 404 - Chat Not Found
**Request:**
```
GET /api/v1/chats/00000000-0000-0000-0000-000000000000
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Chat not found",
  "path": "/api/v1/chats/00000000-0000-0000-0000-000000000000"
}
```

### 404 - Message Not Found
**Request:**
```
GET /api/v1/messages/00000000-0000-0000-0000-000000000000/reactions
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Message not found",
  "path": "/api/v1/messages/00000000-0000-0000-0000-000000000000/reactions"
}
```

### 403 - User Not Participant
**Request:**
```json
{
  "chatId": "550e8400-e29b-41d4-a716-446655440000",
  "senderId": "00000000-0000-0000-0000-000000000000",
  "content": "Trying to send message without being a participant"
}
```

**Response (403 Forbidden):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "User is not a participant in this chat",
  "path": "/api/v1/messages"
}
```

### 400 - Invalid Reply-To Message
**Request:**
```json
{
  "chatId": "550e8400-e29b-41d4-a716-446655440000",
  "senderId": "123e4567-e89b-12d3-a456-426614174000",
  "content": "Trying to reply to message from different chat",
  "replyToId": "999e4567-e89b-12d3-a456-426614174999"
}
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Reply-to message does not belong to this chat",
  "path": "/api/v1/messages"
}
```

---

## Notes

1. **UUIDs**: Replace the example UUIDs with actual UUIDs from your database:
   - `chatId`: Use a valid chat ID from your `chats` table
   - `userId`, `senderId`: Use valid user IDs from your `users` table
   - `messageId`: Use a valid message ID from your `messages` table
   - `songId`: Use a valid song ID from your `songs` table (for song messages)

2. **Testing Flow**:
   - First, get valid `userId` values from your database
   - Create a chat with multiple participants
   - Create messages in the chat
   - Add reactions to messages
   - Mark messages as read
   - Test pagination with the `/page` endpoint

3. **Pagination**:
   - Use `/api/v1/messages/chat/{chatId}/page` for paginated results
   - `limit` parameter is optional (default 20) and clamped between 1 and 100
   - Use `cursor` from `nextCursor` in the response to get the next page
   - When `hasNext` is `false`, there are no more pages
   - Ordering is stable: `created_at DESC, id DESC` (keyset pagination)

4. **Chat Types**:
   - `DIRECT`: One-on-one chat between two users
   - `GROUP`: Group chat with multiple participants

5. **Message Types**:
   - `TEXT`: Regular text message
   - `SONG`: Message with a song attachment
   - Other types as defined in your `MessageType` enum

6. **Reactions**:
   - Emoji should be URL-encoded when used in DELETE endpoint URLs
   - Each user can have multiple reactions on the same message (different emojis)
   - Same user can't have duplicate reactions (same emoji) - handled by primary key constraint

7. **Read Receipts**:
   - Marking a message as read also updates the participant's `last_read_message_id`
   - This helps track which messages have been read by each participant

8. **Port**: Adjust the port in the URL if your application runs on a different port (default is 8080)
