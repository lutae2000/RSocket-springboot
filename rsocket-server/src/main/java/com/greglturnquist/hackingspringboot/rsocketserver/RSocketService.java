package com.greglturnquist.hackingspringboot.rsocketserver;

import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import reactor.core.publisher.*;

@Controller
public class RSocketService {
    private final ItemRepository itemRepository;

    //private final FluxSink<Item> itemSink;
    private final Sinks.Many<Item> itemSink;

    public RSocketService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.itemSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    /**
     * 단건 형식으로된 데이터를 RSocket 을 통해 전송
     * @param item
     * @return
     */
    @MessageMapping("newItems.request-response")
    public Mono<Item> processNewItemsViaRSocketRequestResponse(Item item){
        return this.itemRepository.save(item)
                .doOnNext(saveItem -> this.itemSink.tryEmitNext(saveItem));
    }

    /**
     * List 형식으로된 여러건 데이터를 RSocket 을 통해 전송
     * @return
     */
    @MessageMapping("newItems.request-stream")
    public Flux<Item> findItemsViaSocketRequestStream(){
        return this.itemRepository.findAll()
                .doOnNext(this.itemSink::tryEmitNext);
    }

    @MessageMapping("newItems.fire-and-forget")
    public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item){
        return this.itemRepository.save(item)
                .doOnNext(saveItem -> this.itemSink.tryEmitNext(saveItem))
                .then();
    }

    @MessageMapping("newItems.monitor")
    public Flux<Item> monitorNewItems(){
        return this.itemSink.asFlux();
    }
}
