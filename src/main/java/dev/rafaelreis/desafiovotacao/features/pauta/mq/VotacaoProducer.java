package dev.rafaelreis.desafiovotacao.features.pauta.mq;

import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class VotacaoProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VotacaoProducer.class);

    private final AmqpTemplate amqpTemplate;

    @Value("${queue.name}")
    private String queue;

    public VotacaoProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void enviar(VotoDto voto){
        this.amqpTemplate.convertAndSend(queue, voto);
        LOGGER.debug("Enviando voto: {}", voto);
    }

}
