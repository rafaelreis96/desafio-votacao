package dev.rafaelreis.desafiovotacao.features.pauta.mq;

import dev.rafaelreis.desafiovotacao.features.pauta.dto.VotoDto;
import dev.rafaelreis.desafiovotacao.features.pauta.service.PautaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class VotacaoReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(VotacaoReceiver.class);

    private final PautaService pautaService;

    public VotacaoReceiver(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @RabbitListener(queues = "${queue.name}")
    void receber(VotoDto voto) {
        LOGGER.debug("Recebendo voto: {}", voto);
        pautaService.computarVoto(voto);
    }
}