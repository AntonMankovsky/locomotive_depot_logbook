#!/bin/bash

MIGRATION_FILENAME="./src/main/resources/db/migration/V3__repair_periods_table_initial_data.sql"

function overwrite_warning {
    if test -f "$MIGRATION_FILENAME"; then
        read -p "Файл миграции \"$MIGRATION_FILENAME\" будет перезаписан.
Введите \"+\" чтобы продолжить, или любой другой символ чтобы отменить операцию > " input
    else
        return
    fi
    
    if [ "$input" != '+' ]; then
        echo 'Операция отменена'
        exit 0
    else
        echo "" > "$MIGRATION_FILENAME"
    fi
}

overwrite_warning

# Prepare SQL command
TABLE_NAME="repair_periods"
COMMAND_HEADER="INSERT INTO $TABLE_NAME(
    loco_model_name,
	three_maintenance,
	one_current_repair,
	two_current_repair,
	three_current_repair,
	medium_repair,
	overhaul)
	VALUES(
    "
COMMAND_FOOTER=");"

# Model names
TAM="ТЭМ"; TAM2Y="ТЭМ2У"; TAM2M="ТЭМ2М"; TAM2YM="ТЭМ2УМ"; TAM15="ТЭМ15"
TAM18="ТЭМ18"

TGM4_A="ТГМ4(А)"; TGM4="ТГМ4"; TGM4A="ТГМ4А"

TGM4_B="ТГМ4(Б)"; TGM4B="ТГМ4Б"; TGM4BL="ТГМ4Бл"

NAMES_A=($TAM $TAM2Y $TAM2M $TAM2YM $TAM15 $TAM18)
NAMES_B=($TGM4_A $TGM4 $TGM4A)
NAMES_C=($TGM4_B $TGM4B $TGM4BL)

# Repair periods (days)
THREE_MAINTENANCE=30

ONE_CURRENT_REPAIR[0]=225
ONE_CURRENT_REPAIR[1]=180

TWO_CURRENT_REPAIR[0]=450
TWO_CURRENT_REPAIR[1]=540
TWO_CURRENT_REPAIR[2]=900


THREE_CURRENT_REPAIR[0]=900
THREE_CURRENT_REPAIR[1]=1080
THREE_CURRENT_REPAIR[2]=1800

MEDIUM_REPAIR[0]=2160
MEDIUM_REPAIR[1]=2700

OVERHAUL[0]=4320
OVERHAUL[1]=5400

# Write commands into migration file
temp_command=""
for name in ${NAMES_A[@]}; do
    temp_command="$COMMAND_HEADER
    \"$name\",
    $THREE_MAINTENANCE,
    ${ONE_CURRENT_REPAIR[0]},
    ${TWO_CURRENT_REPAIR[0]},
    ${THREE_CURRENT_REPAIR[0]},
    ${MEDIUM_REPAIR[0]},
    ${OVERHAUL[0]}
    $COMMAND_FOOTER";
    echo "$temp_command" >> "$MIGRATION_FILENAME"
done

for name in ${NAMES_B[@]}; do
    temp_command="$COMMAND_HEADER
    \"$name\",
    $THREE_MAINTENANCE,
    ${ONE_CURRENT_REPAIR[1]},
    ${TWO_CURRENT_REPAIR[1]},
    ${THREE_CURRENT_REPAIR[1]},
    ${MEDIUM_REPAIR[0]},
    ${OVERHAUL[0]}
    $COMMAND_FOOTER";
    echo "$temp_command" >> "$MIGRATION_FILENAME"
done

for name in ${NAMES_C[@]}; do
    temp_command="$COMMAND_HEADER
    \"$name\",
    $THREE_MAINTENANCE,
    ${ONE_CURRENT_REPAIR[1]},
    ${TWO_CURRENT_REPAIR[2]},
    ${THREE_CURRENT_REPAIR[2]},
    ${MEDIUM_REPAIR[1]},
    ${OVERHAUL[1]}
    $COMMAND_FOOTER";
    echo "$temp_command" >> "$MIGRATION_FILENAME"
done
