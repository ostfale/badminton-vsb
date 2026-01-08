package de.ostfale.va.framework.out;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentAgeClassDisciplines;
import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentCategoriesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentTypesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.TournamentAgeClassesVO;
import de.ostfale.va.application.port.out.ForParsingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlannedTournamentsCSVParser implements ForParsingPlannedTournaments, UseLogging {

    private static final String EINZEL = "Einzel";
    private static final String DOPPEL = "Doppel";
    private static final String MIXED = "Mixed";
    private static final String HEADER_START_MARKER = "Start-Datum";
    private static final String CSV_SEPARATOR = ";";
    private static final String EMPTY_STRING = "";
    private static final int DEFAULT_TOURNAMENT_ORDER = 0;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final int START_DATE_INDEX = 0;
    private static final int END_DATE_INDEX = 1;
    private static final int TOURNAMENT_NAME_INDEX = 2;
    private static final int TOURNAMENT_TYPE_INDEX = 3;
    private static final int TOURNAMENT_ORD_NO_INDEX = 4;
    private static final int COUNTRY_INDEX = 5;
    private static final int LOCATION_INDEX = 6;
    private static final int POSTAL_CODE_INDEX = 7;
    private static final int REGION_INDEX = 8;
    private static final int OPEN_NAME_INDEX = 9;
    private static final int ORGANIZER_INDEX = 10;
    private static final int CATEGORY_INDEX = 11;
    private static final int CLOSE_DATE_INDEX = 12;
    private static final int WEB_URL_INDEX = 13;
    private static final int PDF_URL_INDEX = 14;
    private static final int PDF_AVAILABLE_INDEX = 15;
    private static final int AK_U9_INDEX = 20;
    private static final int AK_U11_INDEX = 21;
    private static final int AK_U13_INDEX = 22;
    private static final int AK_U15_INDEX = 23;
    private static final int AK_U17_INDEX = 24;
    private static final int AK_U19_INDEX = 25;
    private static final int AK_U22_INDEX = 26;
    private static final int AK_O19_INDEX = 27;
    private static final int AK_O35_INDEX = 27;


    @Override
    public List<PlannedTournament> parsePlannedTournaments(InputStream inputStream) {
        log().debug("PlannedTournamentsCSVParser :: Parsing CSV file");

        var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try (var bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                    .filter(line -> !isHeaderOrEmptyLine(line))
                    .map(this::parseRow)
                    .toList();
        } catch (IOException e) {
            log().error("PlannedTournamentsCSVParser :: Failed to parse CSV file", e);
            return List.of();
        }
    }

    private PlannedTournament parseRow(String row) {
        String[] splitRow = fixRow(row).split(CSV_SEPARATOR);

        return new PlannedTournament(
                parseLocalDate(readCSVValue(splitRow, START_DATE_INDEX)),
                parseLocalDate(readCSVValue(splitRow, END_DATE_INDEX)),
                readCSVValue(splitRow, TOURNAMENT_NAME_INDEX),
                PlannedTournamentTypesVO.fromDisplayString(splitRow[TOURNAMENT_TYPE_INDEX]),
                getTournamentOrderNo(splitRow),
                readCSVValue(splitRow, COUNTRY_INDEX),
                readCSVValue(splitRow, LOCATION_INDEX),
                readCSVValue(splitRow, POSTAL_CODE_INDEX),
                readCSVValue(splitRow, REGION_INDEX),
                readCSVValue(splitRow, OPEN_NAME_INDEX),
                readCSVValue(splitRow, ORGANIZER_INDEX),
                getCategory(splitRow),
                readCSVValue(splitRow, CLOSE_DATE_INDEX),
                readCSVValue(splitRow, WEB_URL_INDEX),
                readCSVValue(splitRow, PDF_URL_INDEX),
                readCSVValue(splitRow, PDF_AVAILABLE_INDEX),
                buildAgeClassDisciplinesList(splitRow)
        );
    }

    private List<PlannedTournamentAgeClassDisciplines> buildAgeClassDisciplinesList(String[] splitRow) {
        List<PlannedTournamentAgeClassDisciplines> disciplines = new ArrayList<>();
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.U9, readCSVValue(splitRow, AK_U9_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.U11, readCSVValue(splitRow, AK_U11_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.U13, readCSVValue(splitRow, AK_U13_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.U15, readCSVValue(splitRow, AK_U15_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.U17, readCSVValue(splitRow, AK_U17_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.U19, readCSVValue(splitRow, AK_U19_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.U22, readCSVValue(splitRow, AK_U22_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.O19, readCSVValue(splitRow, AK_O19_INDEX)));
        disciplines.add(buildAgeClassDisciplines(TournamentAgeClassesVO.O35, readCSVValue(splitRow, AK_O35_INDEX)));
        return disciplines;
    }

    private LocalDate parseLocalDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    private PlannedTournamentCategoriesVO getCategory(String[] splitRow) {
        String category = splitRow[CATEGORY_INDEX];
        return PlannedTournamentCategoriesVO.fromDisplayName(category);
    }

    private String readCSVValue(String[] splitRow, int index) {
        if (index >= splitRow.length) {
            return EMPTY_STRING;
        }
        return splitRow[index].trim();
    }

    private PlannedTournamentAgeClassDisciplines buildAgeClassDisciplines(TournamentAgeClassesVO ageClass, String ageClassDisciplines) {
        return new PlannedTournamentAgeClassDisciplines(ageClass, isSingle(ageClassDisciplines), isDouble(ageClassDisciplines), isMixed(ageClassDisciplines));
    }

    private int getTournamentOrderNo(String[] splitRow) {
        String tournamentOrderNo = splitRow[TOURNAMENT_ORD_NO_INDEX];

        if (tournamentOrderNo.isBlank()) {
            return DEFAULT_TOURNAMENT_ORDER;
        }

        if (!isNumeric(tournamentOrderNo)) {
            return DEFAULT_TOURNAMENT_ORDER;
        }

        return Integer.parseInt(tournamentOrderNo);
    }

    boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.trim().matches("\\d+");
    }

    private boolean isHeaderOrEmptyLine(String line) {
        return line.startsWith(HEADER_START_MARKER) || line.isBlank();
    }

    private String fixRow(String row) {
        return row.replace("\"", "");
    }

    private boolean isMixed(String ageClassDisciplines) {
        return ageClassDisciplines.contains(MIXED);
    }

    private boolean isDouble(String ageClassDisciplines) {
        return ageClassDisciplines.contains(DOPPEL);
    }

    private boolean isSingle(String ageClassDisciplines) {
        return ageClassDisciplines.contains(EINZEL);
    }
}
