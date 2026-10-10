'use strict';

/**
 * Generates V19__seed_users_master_data_wh.sql — 50,000 rows for
 * NXTGEN_USERS_MASTER_DATA_WH with guaranteed-unique last names,
 * full names and @nxtgen.com email addresses.
 *
 * Run with: node scripts/generate-users-master-data-wh-seed.js
 */

const fs = require('fs');
const path = require('path');

// Deterministic PRNG (mulberry32) so the generated dataset is reproducible.
function mulberry32(seed) {
    return function () {
        seed |= 0;
        seed = (seed + 0x6d2b79f5) | 0;
        let t = Math.imul(seed ^ (seed >>> 15), 1 | seed);
        t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
        return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
    };
}

const rand = mulberry32(42);

function shuffle(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(rand() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
}

const FIRST_NAMES = [
    'James','Mary','Robert','Patricia','John','Jennifer','Michael','Linda','David','Elizabeth',
    'William','Barbara','Richard','Susan','Joseph','Jessica','Thomas','Sarah','Charles','Karen',
    'Christopher','Nancy','Daniel','Lisa','Matthew','Margaret','Anthony','Betty','Mark','Sandra',
    'Donald','Ashley','Steven','Kimberly','Paul','Emily','Andrew','Donna','Joshua','Michelle',
    'Kenneth','Carol','Kevin','Amanda','Brian','Dorothy','George','Melissa','Edward','Deborah',
    'Ronald','Stephanie','Timothy','Rebecca','Jason','Sharon','Jeffrey','Laura','Ryan','Cynthia',
    'Jacob','Kathleen','Gary','Amy','Nicholas','Shirley','Eric','Angela','Jonathan','Helen',
    'Stephen','Anna','Larry','Brenda','Justin','Pamela','Scott','Nicole','Brandon','Emma',
    'Benjamin','Samantha','Samuel','Katherine','Gregory','Christine','Frank','Debra','Alexander','Rachel',
    'Raymond','Catherine','Patrick','Carolyn','Jack','Janet','Dennis','Ruth','Jerry','Maria',
    'Tyler','Heather','Aaron','Diane','Jose','Virginia','Adam','Julie','Nathan','Joyce',
    'Henry','Victoria','Douglas','Olivia','Zachary','Kelly','Peter','Christina','Kyle','Lauren',
    'Walter','Joan','Ethan','Evelyn','Jeremy','Judith','Harold','Megan','Keith','Cheryl',
    'Christian','Andrea','Roger','Hannah','Noah','Martha','Gerald','Jacqueline','Carl','Frances',
    'Terry','Gloria','Sean','Ann','Austin','Teresa','Arthur','Kathryn','Lawrence','Sara',
    'Jesse','Janice','Dylan','Jean','Bryan','Alice','Joe','Madison','Jordan','Doris',
    'Billy','Abigail','Bruce','Julia','Albert','Judy','Willie','Grace','Gabriel','Denise',
    'Logan','Amber','Alan','Marilyn','Juan','Beverly','Wayne','Danielle','Roy','Theresa',
    'Ralph','Sophia','Randy','Marie','Eugene','Diana','Vincent','Brittany','Russell','Natalie',
    'Elijah','Isabella','Louis','Charlotte','Bobby','Rose','Philip','Alexis','Johnny','Kayla'
];

const LAST_NAME_POOL = [
    'Smith','Johnson','Williams','Brown','Jones','Garcia','Miller','Davis','Rodriguez','Martinez',
    'Hernandez','Lopez','Gonzalez','Wilson','Anderson','Thomas','Taylor','Moore','Jackson','Martin',
    'Lee','Perez','Thompson','White','Harris','Sanchez','Clark','Ramirez','Lewis','Robinson',
    'Walker','Young','Allen','King','Wright','Scott','Torres','Nguyen','Hill','Flores',
    'Green','Adams','Nelson','Baker','Hall','Rivera','Campbell','Mitchell','Carter','Roberts',
    'Gomez','Phillips','Evans','Turner','Diaz','Parker','Cruz','Edwards','Collins','Reyes',
    'Stewart','Morris','Morales','Murphy','Cook','Rogers','Gutierrez','Ortiz','Morgan','Cooper',
    'Peterson','Bailey','Reed','Kelly','Howard','Ramos','Kim','Cox','Ward','Richardson',
    'Watson','Brooks','Chavez','Wood','James','Bennett','Gray','Mendoza','Ruiz','Hughes',
    'Price','Alvarez','Castillo','Sanders','Patel','Myers','Long','Ross','Foster','Jimenez',
    'Powell','Jenkins','Perry','Russell','Sullivan','Bell','Coleman','Butler','Henderson','Barnes',
    'Gonzales','Fisher','Vasquez','Simmons','Romero','Jordan','Patterson','Alexander','Hamilton','Graham',
    'Reynolds','Griffin','Wallace','Moreno','West','Cole','Hayes','Gibson','Bryant','Ellis',
    'Stevens','Murray','Ford','Marshall','Owens','Mcdonald','Harrison','Kennedy','Wells','Alvarado',
    'Woods','Mendez','Castro','Fernandez','Grant','Hunter','Duncan','Hicks','Spencer','Porter',
    'Stone','Guzman','Bishop','Day','Meyer','Perkins','Tucker','Holmes','Fuller','Hansen',
    'Warren','Fields','Walsh','Knight','Cortez','Vargas','Parks','Carr','Soto','Hoffman',
    'Dixon','Peters','Snyder','Cunningham','Delgado','Burke','Gardner','Marsh','Nichols','Webb',
    'Olson','Freeman','Chapman','Shaw','Blake','Bradley','Horton','Franklin','Sandoval','Cervantes',
    'Zimmerman','Love','Riley','Pierce','Sweeney','Bowman','Dean','Weaver','Dunn','Stephens',
    'Payne','Harvey','Berry','Hudson','Lambert','Wolfe','French','Cummings','Hartman','Mason',
    'Gross','Wagner','Lynch','Pearson','Hunt','Lawson','Walters','Little','Welch','Pratt',
    'Holloway','Sharp','Werner','Hoover','Mccoy','Hodges','Wheeler','Mcbride','Nash','Lutz',
    'Mckay','Tanner','Odell','Rush','Mccall','Snow','Tran','Terry','Short','Goodwin',
    'Osborne','Buckley','Vega','Pacheco','Robbins','Rowe','Cantrell','Sosa','Mayo','Mullins'
];

function slugify(value) {
    return value.toLowerCase().replace(/[^a-z0-9]/g, '');
}

function slugifyKeepHyphen(value) {
    return value.toLowerCase().replace(/[^a-z0-9-]/g, '');
}

const TARGET_ROW_COUNT = 50000;
const BATCH_SIZE = 500;

const surnamePairs = [];
for (let i = 0; i < LAST_NAME_POOL.length; i++) {
    for (let j = 0; j < LAST_NAME_POOL.length; j++) {
        if (i !== j) {
            surnamePairs.push([i, j]);
        }
    }
}

shuffle(surnamePairs);

if (surnamePairs.length < TARGET_ROW_COUNT) {
    throw new Error(`Not enough unique surname combinations: ${surnamePairs.length}`);
}

const rows = [];
for (let idx = 0; idx < TARGET_ROW_COUNT; idx++) {
    const [i, j] = surnamePairs[idx];
    const firstName = FIRST_NAMES[idx % FIRST_NAMES.length];
    const lastName = `${LAST_NAME_POOL[i]}-${LAST_NAME_POOL[j]}`;
    const email = `${slugify(firstName)}.${slugifyKeepHyphen(lastName)}@nxtgen.com`;
    const isActive = rand() < 0.85 ? 'Y' : 'N';
    rows.push([firstName, lastName, email, isActive]);
}

const lines = [
    '-- Bulk seed data for NXTGEN_USERS_MASTER_DATA_WH: 50,000 rows with unique',
    '-- last names (double-barrelled from a real-surname pool, so no surname ever',
    '-- repeats) and unique @nxtgen.com email addresses. Generated deterministically',
    '-- (fixed PRNG seed) so re-running the generator script reproduces this file.',
];

for (let start = 0; start < rows.length; start += BATCH_SIZE) {
    const batch = rows.slice(start, start + BATCH_SIZE);
    const valuesSql = batch
        .map(([firstName, lastName, email, isActive]) =>
            `('${firstName}','${lastName}','${email}','${isActive}')`
        )
        .join(',\n');
    lines.push(
        `INSERT INTO NXTGEN_USERS_MASTER_DATA_WH (FIRST_NM, LAST_NM, EMAIL_ADDRESS, IS_ACTIVE) VALUES\n${valuesSql};`
    );
}

const outputPath = path.join(
    __dirname,
    '..',
    'src',
    'main',
    'resources',
    'db',
    'migration',
    'V19__seed_users_master_data_wh.sql'
);

fs.writeFileSync(outputPath, lines.join('\n') + '\n', 'utf8');
console.log(`Wrote ${rows.length} rows to ${outputPath}`);
