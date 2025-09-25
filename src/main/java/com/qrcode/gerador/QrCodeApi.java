// Gerador de QR Code em Java, utilizando a biblioteca QRGen (https://github.com/kenglxn/QRGen)

package com.qrcode.gerador;

// --- BIBLIOTECAS PARA CRIAR O SERVIDOR WEB ---
import com.sun.net.httpserver.HttpExchange; // Para lidar com a troca de informações (requisição/resposta) HTTP
import com.sun.net.httpserver.HttpHandler; // Para criar o "manipulador" que processa as requisições
import com.sun.net.httpserver.HttpServer; // A classe principal que representa nosso servidor web

// --- BIBLIOTECA PARA GERAR O QR CODE ---
import net.glxn.qrgen.javase.QRCode; // A biblioteca que faz a mágica de criar o QR Code

// --- BIBLIOTECAS PADRÃO DO JAVA ---
import java.io.ByteArrayOutputStream; // Para guardar a imagem do QR Code em memória antes de enviar
import java.io.OutputStream; // Para enviar a resposta (a imagem) para o navegador
import java.net.InetSocketAddress; // Para definir o endereço (IP e porta) do nosso servidor
import java.net.URLDecoder; // Para "traduzir" caracteres especiais que vêm na URL (ex: %20 vira espaço)
import java.nio.charset.StandardCharsets; // Para garantir que estamos usando o padrão de caracteres correto (UTF-8)

/**
 * Essa é a classe principal da API
 */
public class QrCodeApi {
    public static void main(String[] args) {
        // Vou usar a porta 8080. Parece que todo mundo usa essa pra testar as coisas
        int port = 8080;

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            // Isso aqui cria o "endereço" /qrcode. Quando alguém acessar, o código do QrCodeHandler vai ser executado
            server.createContext("/qrcode", new QrCodeHandler());
            // Liga o servidor! Agora ele fica esperando alguém chamar
            server.start();
            // Imprime uma mensagem especial no console para o Prof Alex
            System.out.println("Saudações, Professor Alex Holanda! Tudo bem? Esse script em Java foi feito para transformar qualquer coisa em um QR Code! Como exemplo, coloquei abaixo um link acessível direcionando ao site do senhor.");
            System.out.println("Acesse: http://localhost:8080/qrcode?texto=https://dev.alexholanda.com.br/poo-java/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Essa classe aqui é a que vai fazer o trabalho de verdade quando alguém chamar o endereço /qrcode
     */
    static class QrCodeHandler implements HttpHandler {

        /**
         * Esse método é chamado toda vez que alguém entra em /qrcode
         * @param exchange Contém todas as informações da requisição (quem pediu, o que pediu, etc.)
         *                 e também é usado para enviar a resposta
         */
    @Override
    public void handle(HttpExchange exchange) {
            try {
                String textoParaQrCode = "Use o parâmetro 'texto' na URL.";
                String query = exchange.getRequestURI().getQuery(); // Pega a parte da URL depois do '?'
                if (query != null && query.startsWith("texto=")) {
                    textoParaQrCode = URLDecoder.decode(query.substring(6), StandardCharsets.UTF_8);
                }

                // Gera o QR Code (sem tratamento de erro)
                ByteArrayOutputStream pngOutputStream = QRCode.from(textoParaQrCode)
                        .withSize(250, 250)
                        .stream();

                // Retorna a imagem para o navegador
                exchange.getResponseHeaders().set("Content-Type", "image/png");
                exchange.sendResponseHeaders(200, pngOutputStream.size());
                OutputStream responseBody = exchange.getResponseBody();
                pngOutputStream.writeTo(responseBody);
                responseBody.close();
                pngOutputStream.close();
                System.out.println("Sucesso na geração da imagem: " + textoParaQrCode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
