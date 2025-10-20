import http from 'k6/http';
import { sleep, check } from 'k6';
import { Trend } from 'k6/metrics';

// 📊 Définition d'une métrique personnalisée pour suivre la latence des requêtes
const latencyTrend = new Trend('custom_latency', true);

// ⚙️ Configuration globale du test
export const options = {
    stages: [
        { duration: '30s', target: 10 },   // Étape 1 : montée progressive à 10 utilisateurs simultanés
        { duration: '1m', target: 50 },    // Étape 2 : charge modérée stable
        { duration: '1m', target: 200 },   // Étape 3 : forte charge (stress test)
        { duration: '30s', target: 0 },    // Étape 4 : redescente progressive
    ],
    thresholds: {
        // ✅ Objectif 1 : moins de 5% de requêtes en échec
        http_req_failed: ['rate<0.05'],
        // ✅ Objectif 2 : latence p95 inférieure à 1000 ms (1s)
        http_req_duration: ['p(95)<1000'],
    },
};

// 🧠 Fonction exécutée par chaque utilisateur virtuel (VU)
export default function () {
    // URL cible du service à tester (DNS interne Kubernetes)
    const baseUrl = 'http://submission-service.challengr.svc.cluster.local:8081';

    // Chaque utilisateur virtuel (VU) utilise un nom différent
    const user = `test${__VU}`;

    // Corps JSON de la requête HTTP POST
    const payload = JSON.stringify({
        challengeId: 1,
        userHandle: user
    });

    // En-têtes HTTP
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer fake-token', // 🔒 car mon endpoint nécessite un token
        },
    };

    // 📡 Envoi d'une requête POST vers l'API du submission-service
    const res = http.post(`${baseUrl}/api/submissions`, payload, params);

    // 🕒 Ajout de la durée de la requête à la métrique personnalisée
    latencyTrend.add(res.timings.duration);

    // ✅ Vérification de la réponse
    check(res, {
        'status is 200': (r) => r.status === 200,                // La requête doit réussir
        'response time < 2s': (r) => r.timings.duration < 2000,  // Temps de réponse raisonnable
    });

    // 💤 Petite pause entre deux requêtes
    sleep(0.2);
}
