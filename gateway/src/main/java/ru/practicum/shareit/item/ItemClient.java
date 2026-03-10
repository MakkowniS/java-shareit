package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItemToRent(Long userId, String text) {
        Map<String, Object> params = Map.of("text", text);
        return get("/search", userId, params);
    }

    public ResponseEntity<Object> saveItem(Long userId, ItemDtoRequest dtoRequest) {
        return post("", userId, dtoRequest);
    }

    public ResponseEntity<Object> saveComment(Long userId, Long itemId ,CommentDtoRequest dtoRequest) {
        return post("/" + itemId + "/comment", userId, dtoRequest);
    }

    public ResponseEntity<Object> editItem(Long userId, Long itemId, ItemDtoRequest dtoRequest) {
        return patch("/" + itemId, userId, dtoRequest);
    }
}
