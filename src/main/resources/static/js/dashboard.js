// Utility function to get JWT token from localStorage
const getToken = () => localStorage.getItem('jwtToken');

// Utility function for making authenticated API requests
const fetchWithAuth = async (url, options = {}) => {
    const token = getToken();
    if (!token) {
        console.error('No token found, redirecting to login.');
        window.location.href = '/login';
        return null; // Indicate failure or throw an error
    }

    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json' // Assume JSON for most requests
    };

    try {
        const response = await fetch(url, { ...options, headers });
        if (response.status === 401) { // Unauthorized
            console.error('Unauthorized access, redirecting to login.');
            localStorage.removeItem('jwtToken');
            window.location.href = '/login';
            return null;
        }
        if (!response.ok) {
            // Handle other errors (e.g., 400, 404, 500)
            console.error(`API Error: ${response.status} ${response.statusText}`);
            // Try to get error message from body
            try {
                const errorData = await response.json();
                console.error('Error details:', errorData);
                // Optionally display error to user
            } catch (e) {
                console.error('Could not parse error response body.');
            }
            return null; // Indicate failure
        }
        // Check if response has content before trying to parse JSON
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return response.json();
        }
        return response; // Return response directly if not JSON
    } catch (error) {
        console.error('Network or fetch error:', error);
        // Handle network errors
        return null; // Indicate failure
    }
};

// Format date/time for display
const formatDateTime = (isoString) => {
    if (!isoString) return 'N/A';
    try {
        return new Date(isoString).toLocaleString();
    } catch (e) {
        return 'Invalid Date';
    }
};

// --- User Profile --- //
const userDetailsDiv = document.getElementById('user-details');
const editUserForm = document.getElementById('editUserForm');
const editUserButton = document.getElementById('editUserButton');
const cancelEditUserButton = document.getElementById('cancelEditUser');
const editUserMessageDiv = document.getElementById('editUserMessage');
let currentUserData = null;

async function loadUserDetails() {
    const data = await fetchWithAuth('/api/user/me');
    if (data) {
        currentUserData = data;
        displayUserDetails(data);
        populateEditForm(data);
    } else {
        userDetailsDiv.innerHTML = '<p class="text-red-500">Failed to load user data.</p>';
    }
}

function displayUserDetails(user) {
    userDetailsDiv.innerHTML = `
        <p><span class="font-semibold">Email:</span> ${user.email || 'N/A'}</p>
        <p><span class="font-semibold">Age:</span> ${user.age || 'N/A'}</p>
        <p><span class="font-semibold">Height:</span> ${user.heightCm ? user.heightCm + ' cm' : 'N/A'}</p>
        <p><span class="font-semibold">Weight:</span> ${user.weightKg ? user.weightKg + ' kg' : 'N/A'}</p>
        <p><span class="font-semibold">Ratio:</span> ${user.insulinRatio ? user.insulinRatio + ' U/10g' : 'N/A'}</p>
        <p><span class="font-semibold">Sensitivity:</span> ${user.sensitivityFactor ? user.sensitivityFactor + ' mg/dL/U' : 'N/A'}</p>
    `;
}

function populateEditForm(user) {
    document.getElementById('editAge').value = user.age || '';
    document.getElementById('editHeightCm').value = user.heightCm || '';
    document.getElementById('editWeightKg').value = user.weightKg || '';
    document.getElementById('editInsulinRatio').value = user.insulinRatio || '';
    document.getElementById('editSensitivityFactor').value = user.sensitivityFactor || '';
}

editUserButton.addEventListener('click', () => {
    userDetailsDiv.classList.add('hidden');
    editUserForm.classList.remove('hidden');
    editUserButton.classList.add('hidden');
    editUserMessageDiv.textContent = ''; // Clear message
});

cancelEditUserButton.addEventListener('click', () => {
    userDetailsDiv.classList.remove('hidden');
    editUserForm.classList.add('hidden');
    editUserButton.classList.remove('hidden');
    // Optionally reset form fields to original values if needed
    if (currentUserData) populateEditForm(currentUserData);
});

editUserForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    editUserMessageDiv.textContent = 'Saving...';
    editUserMessageDiv.className = 'text-xs text-gray-600';

    const updatedData = {
        // Include only fields that are meant to be updated
        age: parseInt(document.getElementById('editAge').value) || null,
        heightCm: parseInt(document.getElementById('editHeightCm').value) || null,
        weightKg: parseFloat(document.getElementById('editWeightKg').value) || null,
        insulinRatio: parseFloat(document.getElementById('editInsulinRatio').value) || null,
        sensitivityFactor: parseFloat(document.getElementById('editSensitivityFactor').value) || null,
        // Do NOT include email or ID here
    };

    const result = await fetchWithAuth('/api/user/me', {
        method: 'PUT',
        body: JSON.stringify(updatedData)
    });

    if (result) {
        currentUserData = result; // Update local cache
        displayUserDetails(result);
        populateEditForm(result); // Update form defaults
        userDetailsDiv.classList.remove('hidden');
        editUserForm.classList.add('hidden');
        editUserButton.classList.remove('hidden');
        editUserMessageDiv.textContent = 'Profile updated successfully!';
        editUserMessageDiv.className = 'text-xs text-green-600';
        // Reload recommendation as user stats changed
        loadRecommendation();
    } else {
        editUserMessageDiv.textContent = 'Failed to update profile.';
        editUserMessageDiv.className = 'text-xs text-red-600';
    }
});

// --- Sidebar Data --- //
const recommendationCard = document.getElementById('recommendation-card');
const mealHistoryUl = document.getElementById('meal-history');
const injectionHistoryUl = document.getElementById('injection-history');

async function loadRecommendation() {
    const data = await fetchWithAuth('/api/recommendation/latest');
    if (data) {
        if (data.totalDose !== null && data.nextInjectionTime !== null) {
            recommendationCard.innerHTML = `
                <p><span class="font-semibold">Dose:</span> ${data.totalDose.toFixed(1)} units</p>
                <p><span class="font-semibold">Next Injection:</span> ${formatDateTime(data.nextInjectionTime)}</p>
                <p class="text-xs text-gray-600 mt-1">Based on meal at ${formatDateTime(data.mealEatenAt)}</p>
                <!-- <p class="text-xs text-gray-500 mt-1">Details: ${data.calculationDetails || ''}</p> -->
            `;
        } else {
            recommendationCard.innerHTML = `<p class="text-orange-600">${data.calculationDetails || 'Cannot calculate recommendation. Check profile and meal data.'}</p>`;
        }
    } else {
        recommendationCard.innerHTML = '<p class="text-red-500">Failed to load recommendation.</p>';
    }
}

async function loadMealHistory() {
    const data = await fetchWithAuth('/api/meal/history'); // Assuming this endpoint exists
    if (data && Array.isArray(data)) {
        if (data.length === 0) {
            mealHistoryUl.innerHTML = '<li>No meals recorded yet.</li>';
            return;
        }
        mealHistoryUl.innerHTML = data.slice(0, 5).map(meal => `
            <li class="text-xs border-b pb-1 mb-1">
                <p>${formatDateTime(meal.eatenAt)}</p>
                <p class="text-gray-700 truncate" title="${meal.description}">${meal.description || 'No description'}</p>
                <p class="text-gray-500">Carbs: ${meal.gramsCarbs !== null ? meal.gramsCarbs.toFixed(1) + 'g' : 'N/A'}</p>
            </li>
        `).join('');
    } else {
        mealHistoryUl.innerHTML = '<li>Failed to load meals.</li>';
    }
}

async function loadInjectionHistory() {
    const data = await fetchWithAuth('/api/injection/history'); // Assuming this endpoint exists
    if (data && Array.isArray(data)) {
        if (data.length === 0) {
            injectionHistoryUl.innerHTML = '<li>No injections recorded yet.</li>';
            return;
        }
        injectionHistoryUl.innerHTML = data.slice(0, 5).map(inj => `
            <li class="text-xs border-b pb-1 mb-1">
                <p>${formatDateTime(inj.injectedAt)}</p>
                <p class="text-gray-700">${inj.units !== null ? inj.units.toFixed(1) + ' units' : 'N/A'}</p>
            </li>
        `).join('');
    } else {
        injectionHistoryUl.innerHTML = '<li>Failed to load injections.</li>';
    }
}

// --- Chat --- //
const chatMessagesDiv = document.getElementById('chat-messages');
const chatForm = document.getElementById('chat-form');
const chatInput = document.getElementById('chat-input');

function addChatMessage(message, isUser = false) {
    const messageDiv = document.createElement('div');
    messageDiv.textContent = message;
    messageDiv.classList.add('p-2', 'rounded', 'text-sm', 'max-w-xs', 'md:max-w-md', 'break-words');
    if (isUser) {
        messageDiv.classList.add('bg-blue-500', 'text-white', 'self-end');
    } else {
        messageDiv.classList.add('bg-gray-200', 'text-gray-800', 'self-start');
    }
    chatMessagesDiv.appendChild(messageDiv);
    // Scroll to bottom
    chatMessagesDiv.scrollTop = chatMessagesDiv.scrollHeight;
}

chatForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const messageText = chatInput.value.trim();
    if (!messageText) return;

    addChatMessage(messageText, true);
    chatInput.value = ''; // Clear input
    chatInput.disabled = true; // Disable input while waiting for response

    // Determine if it's likely a meal entry or a general chat message
    // Simple heuristic: check for keywords like 'eat', 'ate', 'had', 'meal', 'food'
    const isMealEntry = /\b(eat|ate|had|meal|food|lunch|dinner|breakfast|snack)\b/i.test(messageText);

    let endpoint = '/api/chat'; // Default to general chat (proxy to llama.cpp)
    let requestBody = { message: messageText };

    // If it looks like a meal entry, use the meal endpoint
    if (isMealEntry) {
        endpoint = '/api/meal';
        requestBody = { freeTextDescription: messageText };
    }

    try {
        const response = await fetchWithAuth(endpoint, {
            method: 'POST',
            body: JSON.stringify(requestBody)
        });

        if (response) {
            if (isMealEntry) {
                // Meal endpoint returns the created MealDto
                addChatMessage(`Meal logged: ${response.description || 'OK'}. Carbs estimated: ${response.gramsCarbs !== null ? response.gramsCarbs.toFixed(1) + 'g' : 'N/A'}. Check sidebar for recommendation.`, false);
                // Reload sidebar data after logging meal
                loadRecommendation();
                loadMealHistory();
            } else {
                // Chat endpoint (proxy) should return the LLM's response
                // Assuming the proxy returns a simple JSON like { response: "..." }
                addChatMessage(response.response || "Received response, but couldn't parse content.", false);
            }
        } else {
            addChatMessage('Sorry, I encountered an error processing your message.', false);
        }
    } catch (error) {
        console.error('Error sending chat message:', error);
        addChatMessage('Sorry, there was a network error. Please try again.', false);
    } finally {
        chatInput.disabled = false; // Re-enable input
        chatInput.focus();
    }
});

// --- Logout --- //
document.getElementById('logoutButton').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    window.location.href = '/login';
});

// --- Initial Load --- //
function initializeDashboard() {
    if (!getToken()) {
        window.location.href = '/login';
        return;
    }
    loadUserDetails();
    loadRecommendation();
    loadMealHistory();
    loadInjectionHistory();
}

// Run initialization when the script loads
initializeDashboard();

