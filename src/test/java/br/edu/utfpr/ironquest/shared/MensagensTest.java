package br.edu.utfpr.ironquest.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/** Toda chave de mensagem usada no código precisa existir em messages.properties. */
class MensagensTest {

	/** Primeiro literal de exceções de domínio e das chamadas que resolvem mensagens no tratador de erros. */
	private static final Pattern CHAVE = Pattern.compile(
			"(?:Exception|super|mensagem|responder)\\([^\"()]*\"([a-z0-9-]+(?:\\.[a-z0-9-]+)+)\"");

	@Test
	void chavesUsadasNoCodigoExistemNoArquivoDeMensagens() throws IOException {
		Properties mensagens = new Properties();
		try (Reader leitor = Files.newBufferedReader(Path.of("src/main/resources/messages.properties"),
				StandardCharsets.UTF_8)) {
			mensagens.load(leitor);
		}

		Set<String> chaves = new TreeSet<>();
		try (Stream<Path> arquivos = Files.walk(Path.of("src/main/java"))) {
			for (Path arquivo : arquivos.filter(caminho -> caminho.toString().endsWith(".java")).toList()) {
				Matcher matcher = CHAVE.matcher(Files.readString(arquivo, StandardCharsets.UTF_8));
				while (matcher.find()) {
					chaves.add(matcher.group(1));
				}
			}
		}

		assertThat(chaves).as("chaves encontradas no código").hasSizeGreaterThan(20);
		assertThat(chaves).allSatisfy(chave -> assertThat(mensagens).as(chave).containsKey(chave));
	}
}
