# Postman Examples for Comments API

## Base URL
```
http://localhost:8080
```
(Adjust port if your application runs on a different port)

---

## 1. Add a Top-Level Comment

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/comments`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "trackId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "content": "This is a great track! Love the melody."
  }
  ```

**Expected Response (200 OK):**
```json
{
  "id": "789e4567-e89b-12d3-a456-426614174111",
  "track_id": "550e8400-e29b-41d4-a716-446655440000",
  "user_id": "123e4567-e89b-12d3-a456-426614174000",
  "user_nickname": "john_doe",
  "user_avatar_url": "https://example.com/avatar.jpg",
  "parent_id": null,
  "content": "This is a great track! Love the melody.",
  "status": "ACTIVE",
  "likes_count": 0,
  "replies_count": 0,
  "is_liked": false,
  "created_at": "2026-01-03T10:30:00Z",
  "updated_at": null,
  "replies": []
}
```

---

## 2. Add a Reply (Nested Comment)

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/comments`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "trackId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "456e7890-e89b-12d3-a456-426614174222",
    "content": "I totally agree! The production is amazing.",
    "parentId": "789e4567-e89b-12d3-a456-426614174111"
  }
  ```

**Expected Response (200 OK):**
```json
{
  "id": "999e4567-e89b-12d3-a456-426614174333",
  "track_id": "550e8400-e29b-41d4-a716-446655440000",
  "user_id": "456e7890-e89b-12d3-a456-426614174222",
  "user_nickname": "jane_smith",
  "user_avatar_url": null,
  "parent_id": "789e4567-e89b-12d3-a456-426614174111",
  "content": "I totally agree! The production is amazing.",
  "status": "ACTIVE",
  "likes_count": 0,
  "replies_count": 0,
  "is_liked": false,
  "created_at": "2026-01-03T10:35:00Z",
  "updated_at": null,
  "replies": []
}
```

---

## 3. Get Comments for a Track (Without User Context)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/comments/track/550e8400-e29b-41d4-a716-446655440000`

**Expected Response (200 OK):**
```json
[
  {
    "id": "789e4567-e89b-12d3-a456-426614174111",
    "track_id": "550e8400-e29b-41d4-a716-446655440000",
    "user_id": "123e4567-e89b-12d3-a456-426614174000",
    "user_nickname": "john_doe",
    "user_avatar_url": "https://example.com/avatar.jpg",
    "parent_id": null,
    "content": "This is a great track! Love the melody.",
    "status": "ACTIVE",
    "likes_count": 5,
    "replies_count": 2,
    "is_liked": false,
    "created_at": "2026-01-03T10:30:00Z",
    "updated_at": null,
    "replies": [
      {
        "id": "999e4567-e89b-12d3-a456-426614174333",
        "track_id": "550e8400-e29b-41d4-a716-446655440000",
        "user_id": "456e7890-e89b-12d3-a456-426614174222",
        "user_nickname": "jane_smith",
        "user_avatar_url": null,
        "parent_id": "789e4567-e89b-12d3-a456-426614174111",
        "content": "I totally agree! The production is amazing.",
        "status": "ACTIVE",
        "likes_count": 2,
        "replies_count": 0,
        "is_liked": false,
        "created_at": "2026-01-03T10:35:00Z",
        "updated_at": null,
        "replies": []
      }
    ]
  }
]
```

---

## 4. Get Comments for a Track (With User Context - Shows isLiked)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/comments/track/550e8400-e29b-41d4-a716-446655440000?userId=123e4567-e89b-12d3-a456-426614174000`

**Expected Response (200 OK):**
```json
[
  {
    "id": "789e4567-e89b-12d3-a456-426614174111",
    "track_id": "550e8400-e29b-41d4-a716-446655440000",
    "user_id": "123e4567-e89b-12d3-a456-426614174000",
    "user_nickname": "john_doe",
    "user_avatar_url": "https://example.com/avatar.jpg",
    "parent_id": null,
    "content": "This is a great track! Love the melody.",
    "status": "ACTIVE",
    "likes_count": 5,
    "replies_count": 2,
    "is_liked": true,
    "created_at": "2026-01-03T10:30:00Z",
    "updated_at": null,
    "replies": [
      {
        "id": "999e4567-e89b-12d3-a456-426614174333",
        "track_id": "550e8400-e29b-41d4-a716-446655440000",
        "user_id": "456e7890-e89b-12d3-a456-426614174222",
        "user_nickname": "jane_smith",
        "user_avatar_url": null,
        "parent_id": "789e4567-e89b-12d3-a456-426614174111",
        "content": "I totally agree! The production is amazing.",
        "status": "ACTIVE",
        "likes_count": 2,
        "replies_count": 0,
        "is_liked": false,
        "created_at": "2026-01-03T10:35:00Z",
        "updated_at": null,
        "replies": []
      }
    ]
  }
]
```

---

## 5. Get Comments for a Track (Paginated - First Page)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/comments/track/550e8400-e29b-41d4-a716-446655440000/page?limit=20`

**Expected Response (200 OK):**
```json
{
  "items": [
    {
      "id": "789e4567-e89b-12d3-a456-426614174111",
      "track_id": "550e8400-e29b-41d4-a716-446655440000",
      "user_id": "123e4567-e89b-12d3-a456-426614174000",
      "user_nickname": "john_doe",
      "user_avatar_url": "https://example.com/avatar.jpg",
      "parent_id": null,
      "content": "This is a great track! Love the melody.",
      "status": "ACTIVE",
      "likes_count": 5,
      "replies_count": 2,
      "is_liked": false,
      "created_at": "2026-01-03T10:30:00Z",
      "updated_at": null,
      "replies": [
        {
          "id": "999e4567-e89b-12d3-a456-426614174333",
          "track_id": "550e8400-e29b-41d4-a716-446655440000",
          "user_id": "456e7890-e89b-12d3-a456-426614174222",
          "user_nickname": "jane_smith",
          "user_avatar_url": null,
          "parent_id": "789e4567-e89b-12d3-a456-426614174111",
          "content": "I totally agree! The production is amazing.",
          "status": "ACTIVE",
          "likes_count": 2,
          "replies_count": 0,
          "is_liked": false,
          "created_at": "2026-01-03T10:35:00Z",
          "updated_at": null,
          "replies": []
        }
      ]
    }
  ],
  "nextCursor": "eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTAzVDEwOjMwOjAwWiIsImlkIjoiNzg5ZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MTExIn0",
  "hasNext": true
}
```

---

## 6. Get Comments for a Track (Paginated - Next Page)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/comments/track/550e8400-e29b-41d4-a716-446655440000/page?limit=20&cursor=eyJjcmVhdGVkQXQiOiIyMDI2LTAxLTAzVDEwOjMwOjAwWiIsImlkIjoiNzg5ZTQ1NjctZTg5Yi0xMmQzLWE0NTYtNDI2NjE0MTc0MTExIn0`

**Expected Response (200 OK):**
```json
{
  "items": [
    {
      "id": "888e4567-e89b-12d3-a456-426614174444",
      "track_id": "550e8400-e29b-41d4-a716-446655440000",
      "user_id": "789e1234-e89b-12d3-a456-426614174555",
      "user_nickname": "bob_wilson",
      "user_avatar_url": null,
      "parent_id": null,
      "content": "Another great comment!",
      "status": "ACTIVE",
      "likes_count": 3,
      "replies_count": 0,
      "is_liked": false,
      "created_at": "2026-01-03T10:25:00Z",
      "updated_at": null,
      "replies": []
    }
  ],
  "nextCursor": null,
  "hasNext": false
}
```

**Note:** 
- Use the `nextCursor` from the previous response to get the next page
- When `hasNext` is `false` and `nextCursor` is `null`, there are no more pages
- The `limit` parameter is optional (default 20) and is clamped between 1 and 100
- Only top-level comments are paginated; all replies for each top-level comment are included

---

## 7. Get Comments for a Track (Paginated - With User Context)

**Request:**
- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/comments/track/550e8400-e29b-41d4-a716-446655440000/page?userId=123e4567-e89b-12d3-a456-426614174000&limit=20`

**Expected Response (200 OK):**
Same format as above, but with `is_liked` field reflecting whether the specified user has liked each comment.

---

## 8. Add Reaction (Like) to a Comment

**Request:**
- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/comments/reactions`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (JSON):**
  ```json
  {
    "commentId": "789e4567-e89b-12d3-a456-426614174111",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "reaction": "LIKE"
  }
  ```

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

**Note:** The `reaction` field is optional and defaults to "LIKE" if not provided.

---

## 9. Remove Reaction (Unlike) from a Comment

**Request:**
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/comments/789e4567-e89b-12d3-a456-426614174111/reactions/123e4567-e89b-12d3-a456-426614174000`

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

**Note:** After removing a reaction, the `likes_count` in the comment will be automatically updated.

---

## 10. Delete a Comment

**Request:**
- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/comments/789e4567-e89b-12d3-a456-426614174111`

**Expected Response (200 OK):**
Empty response body (204 No Content or 200 OK)

**Note:** 
- This performs a soft delete by setting the comment status to "DELETED"
- Deleted comments will not appear in the comments list (only ACTIVE comments are returned)
- If the deleted comment has a parent, the parent's `replies_count` will be automatically decremented
- The comment's reactions are preserved (cascade delete is not applied for soft deletes)

---

## Error Cases

### 404 - Track Not Found
**Request:**
```json
{
  "trackId": "00000000-0000-0000-0000-000000000000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "content": "This track doesn't exist"
}
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Track not found",
  "path": "/api/v1/comments"
}
```

### 404 - User Not Found
**Request:**
```json
{
  "trackId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "00000000-0000-0000-0000-000000000000",
  "content": "User doesn't exist"
}
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/v1/comments"
}
```

### 400 - Invalid Parent Comment
**Request:**
```json
{
  "trackId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "content": "Trying to reply to non-existent parent",
  "parentId": "00000000-0000-0000-0000-000000000000"
}
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Parent comment not found",
  "path": "/api/v1/comments"
}
```

### 400 - Parent Comment Belongs to Different Track
**Request:**
```json
{
  "trackId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "content": "Trying to reply to comment from different track",
  "parentId": "789e4567-e89b-12d3-a456-426614174111"
}
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Parent comment does not belong to this track",
  "path": "/api/v1/comments"
}
```

### 404 - Comment Not Found (Reaction)
**Request:**
```json
{
  "commentId": "00000000-0000-0000-0000-000000000000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "reaction": "LIKE"
}
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Comment not found",
  "path": "/api/v1/comments/reactions"
}
```

### 404 - Comment Not Found (Delete)
**Request:**
```
DELETE /api/v1/comments/00000000-0000-0000-0000-000000000000
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Comment not found",
  "path": "/api/v1/comments/00000000-0000-0000-0000-000000000000"
}
```

### 400 - Comment Already Deleted
**Request:**
```
DELETE /api/v1/comments/789e4567-e89b-12d3-a456-426614174111
```
(Where the comment status is already "DELETED")

**Response (400 Bad Request):**
```json
{
  "timestamp": "2026-01-03T10:40:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Comment is already deleted",
  "path": "/api/v1/comments/789e4567-e89b-12d3-a456-426614174111"
}
```

---

## Notes

1. **UUIDs**: Replace the example UUIDs with actual UUIDs from your database:
   - `trackId`: Use a valid track/song ID from your `songs` table
   - `userId`: Use a valid user ID from your `users` table
   - `parentId`: Use a valid comment ID from your `track_comment` table (for replies)

2. **Testing Flow**:
   - First, get a valid `trackId` and `userId` from your database
   - Create a top-level comment (without `parentId`)
   - Use the returned comment `id` as `parentId` to create a reply
   - Add a reaction (like) to a comment using the reaction endpoint
   - Fetch comments to see the nested structure and reaction counts
   - Remove a reaction to test the unlike functionality
   - Delete a comment to test soft delete (comment will no longer appear in the list)

3. **User Context**: 
   - When `userId` is provided in the GET request, the response includes `is_liked` field
   - The `likes_count` field shows the total number of reactions on the comment
   - Reactions are automatically included in the `CommentResponse` when fetching comments

4. **Reactions**:
   - Reactions are stored in the `track_comment_reaction` table
   - The `likes_count` in `track_comment` is automatically updated when reactions are added/removed
   - Each user can only have one reaction per comment (enforced by primary key constraint)
   - Default reaction type is "LIKE" but can be customized

5. **Pagination**:
   - Use `/api/v1/comments/track/{trackId}/page` for paginated results
   - Only top-level comments are paginated; all replies for each top-level comment are included
   - `limit` parameter is optional (default 20) and clamped between 1 and 100
   - Use `cursor` from `nextCursor` in the response to get the next page
   - When `hasNext` is `false`, there are no more pages
   - Ordering is stable: `created_at DESC, id DESC` (keyset pagination)
   - The non-paginated endpoint `/api/v1/comments/track/{trackId}` still exists for backward compatibility

6. **Comment Deletion**:
   - Comments are soft-deleted (status set to "DELETED") rather than hard-deleted
   - Deleted comments will not appear in comment lists (only ACTIVE comments are returned)
   - If a parent comment is deleted, its replies_count is automatically decremented
   - Attempting to delete an already deleted comment will return a 400 error

7. **Port**: Adjust the port in the URL if your application runs on a different port (default is 8080)
