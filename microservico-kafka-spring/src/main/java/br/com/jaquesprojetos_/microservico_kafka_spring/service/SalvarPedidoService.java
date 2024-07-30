package br.com.jaquesprojetos_.microservico_kafka_spring.service;

import br.com.jaquesprojetos_.microservico_kafka_spring.data.PedidoData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SalvarPedidoService {
		
		@KafkaListener(topics = "SalvarPedido", groupId = "MicrosservicoSalvaPedido")
		private void executar(ConsumerRecord<String, String> record) {
				
				log.info("Chave = {}", record.key());
				log.info("Cabecalho = {}", record.headers());
				log.info("Particao = {}", record.partition());
				
				String strDados = record.value();
				
				ObjectMapper mapper = new ObjectMapper();
				PedidoData pedido;
				
				try {
						pedido = mapper.readValue(strDados, PedidoData.class);
				} catch (JsonProcessingException ex) {
						log.error("Falha converter evento [dado={}}]", strDados, ex);
						return;
				}
				
				log.info("Evento Recebido = {}", pedido);
				
				
				// Enviar para o outro microsservico
				
				// Responder para a fila de que o pedido foi salvo
				if (gravaBanco(pedido)) {
						log.info("Pedido salvo com sucesso");
				} else {
						log.error("Falha ao salvar pedido");
				}
		}
		
		private boolean gravaBanco(PedidoData pedido) {
				// Gravar no banco de dados
				System.out.println("Gravando no banco de dados: " + pedido);
				return true;
		}
}