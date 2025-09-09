import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
//testando

public class MonitorGlicemia {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String caminhoArquivo = "glicemia.txt";

        System.out.println("=== Monitor de Glicemia ===");
        System.out.println("Digite 'sair' no campo de glicose para encerrar.");

        while (true) {
            System.out.print("Digite o nível de glicose (mg/dL): ");
            String entradaGlicose = scanner.nextLine();
            System.out.println("Foi tomado algum medicamento(Sim/Não): ");
            String medicamento = scanner.nextLine();

            if ("sim".equalsIgnoreCase(medicamento)) {
                System.out.println("Informe o medicamento tomado: ");
                String tipoMedicamento = scanner.nextLine();
                System.out.printf("Informe quantas unidades de %s foram tomadas",medicamento);
                String unidades = scanner.nextLine();
            }

            if ("sair".equalsIgnoreCase(entradaGlicose)) {
                break;
            }

            // Validação simples
            double glicose;
            try {
                glicose = Double.parseDouble(entradaGlicose);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número ou 'sair'.");
                continue;
            }

            LocalDateTime agora = LocalDateTime.now();
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String dataHora = agora.format(formatador);

            // Escrever no arquivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo, true))) {
                writer.write("Data/Hora: " + dataHora + " - Nível de Glicose: " + glicose + " mg/dL");
                writer.newLine();
                System.out.println("Registro salvo com sucesso!\n");
            } catch (IOException e) {
                System.err.println("Erro ao salvar o registro: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Programa encerrado. Dados salvos em 'glicemia.txt'.");
    }
}
