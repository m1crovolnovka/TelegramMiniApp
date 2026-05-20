package com.casino.cards.controller;

import com.casino.cards.dto.response.CardDefinitionResponse;
import com.casino.cards.dto.response.CollectionProgressResponse;
import com.casino.cards.dto.response.InventoryResponse;
import com.casino.cards.service.CardCatalogService;
import com.casino.cards.service.CollectionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardCatalogService cardCatalogService;
    private final CollectionService collectionService;

    @GetMapping
    public List<CardDefinitionResponse> catalog() {
        return cardCatalogService.listDefinitions();
    }

    /** @deprecated use {@link #catalog()} */
    @GetMapping("/definitions")
    public List<CardDefinitionResponse> definitions() {
        return catalog();
    }

    @GetMapping("/inventory")
    public InventoryResponse inventory(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return cardCatalogService.inventory(userId);
    }

    @GetMapping("/collection-progress")
    public CollectionProgressResponse collectionProgress(Authentication authentication) {
        long userId = (Long) authentication.getPrincipal();
        return collectionService.progress(userId);
    }
}
