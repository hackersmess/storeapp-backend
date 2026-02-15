# 🔧 Backend Migration: Rimozione Date da Itinerary

## 📋 Riepilogo Modifiche Backend

### Data: 15 Febbraio 2026

### Motivazione: Eliminare ridondanza - le date vivono solo nel `Group`

---

## ✅ File Modificati

### 1️⃣ **Entity Layer**

#### `Itinerary.java`

```java
// ❌ RIMOSSO
@Column(name = "start_date")
public LocalDate startDate;

@Column(name = "end_date")
public LocalDate endDate;

// ✅ COMMENTATO
// Le date sono gestite dal gruppo (group.vacationStartDate, group.vacationEndDate)
// Non servono più qui per evitare ridondanza
```

**Import rimosso**: `java.time.LocalDate`

---

#### `Group.java`

```java
// ✅ AGGIUNTO @NotNull e nullable = false
@NotNull(message = "La data di inizio vacanza è obbligatoria")
@Column(name = "vacation_start_date", nullable = false)
public LocalDate vacationStartDate;

@NotNull(message = "La data di fine vacanza è obbligatoria")
@Column(name = "vacation_end_date", nullable = false)
public LocalDate vacationEndDate;
```

---

### 2️⃣ **DTO Layer**

#### `ItineraryRequest.java` (Request DTO)

```java
// ❌ RIMOSSO
public LocalDate startDate;
public LocalDate endDate;

// ✅ COMMENTATO
// Le date sono ereditate dal gruppo, non servono qui
```

**Import rimosso**: `java.time.LocalDate`

---

#### `ItineraryDto.java` (Response DTO)

```java
// ❌ RIMOSSO
public LocalDate startDate;
public LocalDate endDate;

// ✅ COMMENTATO
// Le date sono disponibili dal gruppo, non servono qui
```

**Import rimosso**: `java.time.LocalDate`

---

#### `CreateGroupRequest.java`

```java
// ✅ AGGIUNTO @NotNull
@NotNull(message = "La data di inizio vacanza è obbligatoria")
public LocalDate vacationStartDate;

@NotNull(message = "La data di fine vacanza è obbligatoria")
public LocalDate vacationEndDate;
```

**Import aggiunto**: `jakarta.validation.constraints.NotNull`

---

### 3️⃣ **Database Migration**

File: `database/migrations/V008__remove_itinerary_dates.sql`

```sql
-- STEP 1: Verifica che tutti i gruppi abbiano le date
DO $$
DECLARE
    missing_dates_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO missing_dates_count
    FROM groups
    WHERE vacation_start_date IS NULL OR vacation_end_date IS NULL;

    IF missing_dates_count > 0 THEN
        RAISE EXCEPTION 'Ci sono % gruppi senza date.', missing_dates_count;
    END IF;
END $$;

-- STEP 2: Rimuovi colonne da itineraries
ALTER TABLE itineraries DROP COLUMN IF EXISTS start_date;
ALTER TABLE itineraries DROP COLUMN IF EXISTS end_date;

-- STEP 3: Rendi le date del gruppo NOT NULL
ALTER TABLE groups ALTER COLUMN vacation_start_date SET NOT NULL;
ALTER TABLE groups ALTER COLUMN vacation_end_date SET NOT NULL;
```

---

## 🔄 Mapper e Service

### **ItineraryMapper.java** ✅

Nessuna modifica necessaria - MapStruct ignora automaticamente i campi rimossi

### **ItineraryService.java** ✅

Nessuna modifica necessaria - il service non gestiva le date direttamente

### **GroupService.java** ✅

Le validazioni `@NotNull` sono gestite automaticamente da Bean Validation

---

## 🚀 Come Applicare le Modifiche

### 1. Backup Database

```bash
pg_dump storeapp > backup_before_migration.sql
```

### 2. Esegui Migration SQL

```bash
cd database/migrations
psql -U postgres -d storeapp -f V008__remove_itinerary_dates.sql
```

### 3. Ricompila Backend

```bash
cd backend
./mvnw clean compile
```

### 4. Testa gli Endpoint

```bash
# Crea gruppo SENZA date → ❌ deve fallire (400 Bad Request)
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Group",
    "description": "Test"
  }'

# Crea gruppo CON date → ✅ deve funzionare
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Group",
    "description": "Test",
    "vacationStartDate": "2024-06-15",
    "vacationEndDate": "2024-06-22"
  }'

# Crea itinerario (senza date) → ✅ deve funzionare
curl -X POST http://localhost:8080/api/groups/1/itinerary \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Viaggio Roma",
    "description": "Una settimana a Roma"
  }'
```

---

## ✅ Testing Checklist

- [ ] Backup database completato
- [ ] Migration SQL eseguita senza errori
- [ ] Verifica: colonne `start_date` e `end_date` rimosse da `itineraries`
- [ ] Verifica: colonne `vacation_start_date` e `vacation_end_date` ora NOT NULL in `groups`
- [ ] Backend ricompilato senza errori
- [ ] Test: Creazione gruppo senza date → ❌ Errore di validazione
- [ ] Test: Creazione gruppo con date → ✅ Successo
- [ ] Test: Creazione itinerario senza date → ✅ Successo
- [ ] Test: GET itinerario → ✅ Non restituisce più `startDate` e `endDate`
- [ ] Test: GET gruppo → ✅ Restituisce `vacationStartDate` e `vacationEndDate`
- [ ] Frontend aggiornato e funzionante

---

## 🔧 Troubleshooting

### Errore: "column itineraries.start_date does not exist"

**Causa**: Migration non eseguita  
**Soluzione**: Esegui `V008__remove_itinerary_dates.sql`

### Errore: "null value in column vacation_start_date"

**Causa**: Esistono gruppi senza date  
**Soluzione**:

```sql
-- Trova gruppi senza date
SELECT id, name FROM groups
WHERE vacation_start_date IS NULL OR vacation_end_date IS NULL;

-- Imposta date di default o elimina i gruppi di test
UPDATE groups
SET vacation_start_date = CURRENT_DATE,
    vacation_end_date = CURRENT_DATE + INTERVAL '7 days'
WHERE vacation_start_date IS NULL;
```

### Errore di compilazione MapStruct

**Causa**: Cache MapStruct  
**Soluzione**:

```bash
./mvnw clean compile -DskipTests
```

---

## 📊 Impatto API

### Endpoint Modificati

#### ❌ **BREAKING CHANGE** - POST `/api/groups`

**Prima**:

```json
{
	"name": "Gruppo Test",
	"vacationStartDate": "2024-06-15", // opzionale
	"vacationEndDate": "2024-06-22" // opzionale
}
```

**Dopo**:

```json
{
	"name": "Gruppo Test",
	"vacationStartDate": "2024-06-15", // ✅ OBBLIGATORIO
	"vacationEndDate": "2024-06-22" // ✅ OBBLIGATORIO
}
```

**Error Response** (se mancano le date):

```json
{
	"status": 400,
	"message": "Validation failed",
	"errors": {
		"vacationStartDate": "La data di inizio vacanza è obbligatoria",
		"vacationEndDate": "La data di fine vacanza è obbligatoria"
	}
}
```

---

#### ❌ **BREAKING CHANGE** - POST `/api/groups/{groupId}/itinerary`

**Prima**:

```json
{
	"name": "Viaggio Roma",
	"description": "...",
	"startDate": "2024-06-15", // accettato
	"endDate": "2024-06-22" // accettato
}
```

**Dopo**:

```json
{
	"name": "Viaggio Roma",
	"description": "..."
	// ❌ startDate e endDate ignorati/rimossi
}
```

---

#### ✅ **NON BREAKING** - GET `/api/groups/{groupId}/itinerary`

**Prima**:

```json
{
	"id": 1,
	"groupId": 1,
	"name": "Viaggio Roma",
	"startDate": "2024-06-15", // presente
	"endDate": "2024-06-22", // presente
	"activityCount": 5
}
```

**Dopo**:

```json
{
	"id": 1,
	"groupId": 1,
	"name": "Viaggio Roma",
	// ❌ startDate e endDate rimossi
	"activityCount": 5
}
```

**Nota**: Il frontend ora prende le date da `GET /api/groups/{groupId}`

---

## 📝 Note per Sviluppatori

### Come ottenere le date dell'itinerario nel frontend

**❌ Prima**:

```typescript
itineraryService.getItinerary(groupId).subscribe((itinerary) => {
	console.log(itinerary.startDate); // ❌ non esiste più
	console.log(itinerary.endDate); // ❌ non esiste più
});
```

**✅ Dopo**:

```typescript
groupService.getGroup(groupId).subscribe((group) => {
	console.log(group.vacationStartDate); // ✅ usa questo
	console.log(group.vacationEndDate); // ✅ usa questo
});
```

### Pattern consigliato: Caricare gruppo + itinerario insieme

```typescript
forkJoin({
	group: groupService.getGroup(groupId),
	itinerary: itineraryService.getItinerary(groupId),
}).subscribe(({ group, itinerary }) => {
	// Usa group.vacationStartDate e group.vacationEndDate
	// Usa itinerary.activities
});
```

---

## 🎯 Benefici

1. ✅ **Single Source of Truth**: Le date vivono solo nel gruppo
2. ✅ **Meno Ridondanza**: Dati non duplicati
3. ✅ **Validazione Centralizzata**: Le date sono validate alla creazione del gruppo
4. ✅ **API più Pulite**: Meno campi inutili nei DTO
5. ✅ **Database Normalizzato**: Nessuna sincronizzazione necessaria

---

## 🔄 Rollback Plan

Se qualcosa va storto:

```sql
-- 1. Ripristina le colonne
ALTER TABLE itineraries ADD COLUMN start_date DATE;
ALTER TABLE itineraries ADD COLUMN end_date DATE;

-- 2. Popola con le date del gruppo
UPDATE itineraries i
SET start_date = g.vacation_start_date,
    end_date = g.vacation_end_date
FROM groups g
WHERE i.group_id = g.id;

-- 3. Rendi le date del gruppo opzionali di nuovo
ALTER TABLE groups ALTER COLUMN vacation_start_date DROP NOT NULL;
ALTER TABLE groups ALTER COLUMN vacation_end_date DROP NOT NULL;
```

Poi ripristina il codice Java dalla versione precedente.

---

**Implementato il**: 15 Febbraio 2026  
**Breaking Changes**: ✅ Sì (richiede aggiornamento frontend)  
**Rollback Disponibile**: ✅ Sì  
**Testing**: ✅ Richiesto prima del deploy
