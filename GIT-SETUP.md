# 🚀 Setup Git per storeapp-backend

Questo file contiene tutti i comandi Git eseguiti per inizializzare il repository con configurazione locale dell'account personale.

---

## 📋 Prerequisiti

- Repository creato su GitHub: `https://github.com/hackersmess/storeapp-backend`
- Personal Access Token generato su GitHub (con scope `repo`)

---

## 🔧 Comandi eseguiti (in ordine)

### 1️⃣ Inizializzazione del repository Git

```powershell
cd C:\Users\marlombard\personal\storeapp\backend
git init
```

**Cosa fa:** Inizializza un nuovo repository Git nella cartella corrente.

---

### 2️⃣ Configurazione account locale (solo per questo progetto)

```powershell
# Configura email locale (sovrascrive quella globale solo per questo repo)
git config --local user.email "marco.lombardo.git@gmail.com"

# Configura nome utente locale
git config --local user.name "Marco Lombardo"

# Configura username GitHub per le credenziali
git config --local credential.username "hackersmess"
```

**Cosa fa:** Configura le credenziali **solo per questo repository**, senza modificare le configurazioni globali (che rimangono per l'organizzazione).

---

### 3️⃣ Staging dei file (aggiunta alla staging area)

```powershell
git add .
```

**Cosa fa:** Aggiunge tutti i file alla staging area, esclusi quelli nel `.gitignore` (come `target/`, `.idea/`, ecc.).

---

### 4️⃣ Primo commit

```powershell
git commit -m "Initial commit: StoreApp Backend - Quarkus REST API"
```

**Cosa fa:** Crea il primo commit con tutti i file aggiunti.

---

### 5️⃣ Collegamento al repository remoto GitHub

```powershell
# Aggiunge il repository remoto
git remote add origin https://github.com/hackersmess/storeapp-backend.git

# Rinomina il branch da 'master' a 'main' (standard GitHub)
git branch -M main
```

**Cosa fa:** Collega il repository locale a GitHub e rinomina il branch principale.

---

### 6️⃣ Push su GitHub

```powershell
git push -u origin main
```

**Cosa fa:** Carica il codice su GitHub. Ti chiederà:
- **Username:** `hackersmess`
- **Password:** [Il tuo Personal Access Token che inizia con `ghp_...`]

Il flag `-u` imposta `origin/main` come upstream, così i prossimi push saranno solo `git push`.

---

## ✅ Verifica configurazione

### Visualizza configurazione locale
```powershell
git config --local --list
```

### Visualizza configurazione globale
```powershell
git config --global --list
```

### Verifica remote configurato
```powershell
git remote -v
```

### Verifica stato del repository
```powershell
git status
```

---

## 🔄 Workflow quotidiano (dopo il setup iniziale)

```powershell
# 1. Modifica i file nel progetto
# ...

# 2. Visualizza cosa è cambiato
git status

# 3. Aggiungi le modifiche alla staging area
git add .                          # Tutti i file
# oppure
git add path/to/file.java         # File specifico

# 4. Crea un commit con messaggio descrittivo
git commit -m "Descrizione delle modifiche"

# 5. Carica su GitHub
git push
```

---

## 📝 Note importanti

### ✅ Vantaggi di questa configurazione:
- **Questo progetto** usa l'account personale `hackersmess`
- **Tutti gli altri progetti** continuano a usare l'account organizzazione
- Le credenziali sono salvate nel Windows Credential Manager automaticamente dopo il primo push

### 🔐 Sicurezza:
- ✅ `.gitignore` configurato per escludere `target/`, `.idea/`, credenziali, ecc.
- ✅ Password hashate con BCrypt (mai salvate in chiaro)
- ✅ Personal Access Token usato invece della password GitHub

### 📦 File esclusi dal versioning (`.gitignore`):
```
target/              # File compilati Maven
.idea/               # Configurazione IntelliJ
*.iml                # File progetto IntelliJ
.vscode/             # Configurazione VS Code
.env                 # Variabili ambiente locali
*.class              # Bytecode Java
```

---

## 🆘 Comandi utili

### Annulla modifiche non committate
```powershell
git checkout -- NomeFile.java      # Singolo file
git checkout -- .                  # Tutti i file
```

### Rimuovi file dalla staging area (ma mantieni modifiche)
```powershell
git reset HEAD NomeFile.java       # Singolo file
git reset HEAD .                   # Tutti i file
```

### Visualizza history commit
```powershell
git log --oneline                  # Formato compatto
git log --oneline -5               # Ultimi 5 commit
git log --graph --all              # Grafico branches
```

### Visualizza differenze
```powershell
git diff                           # Differenze working directory vs staging
git diff --staged                  # Differenze staging vs ultimo commit
```

### Cambia messaggio ultimo commit (se non ancora pushato)
```powershell
git commit --amend -m "Nuovo messaggio"
```

---

## 🔗 Link utili

- **Repository GitHub:** https://github.com/hackersmess/storeapp-backend
- **Gestione token:** https://github.com/settings/tokens
- **Documentazione Git:** https://git-scm.com/doc

---

## 📊 Risultato finale

✅ Repository locale inizializzato  
✅ Account personale configurato (solo per questo progetto)  
✅ Collegato a GitHub: `hackersmess/storeapp-backend`  
✅ Primo commit pushato (30 files)  
✅ Branch: `main`  
✅ Ready to code! 🚀
