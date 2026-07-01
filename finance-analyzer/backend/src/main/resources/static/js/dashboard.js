const palette = ["#2563eb", "#16a34a", "#f59e0b", "#dc2626", "#7c3aed", "#0891b2", "#db2777"];

function values(data, labelKey, valueKey) {
    return {
        labels: data.map((item) => item[labelKey]),
        amounts: data.map((item) => Number(item[valueKey] || 0))
    };
}

const monthly = values(window.monthlyData || [], "month", "amount");
new Chart(document.getElementById("monthlyChart"), {
    type: "bar",
    data: {
        labels: monthly.labels,
        datasets: [{ label: "Expenses", data: monthly.amounts, backgroundColor: "#2563eb", borderRadius: 6 }]
    },
    options: { responsive: true, plugins: { legend: { display: false } } }
});

const categories = values(window.categoryData || [], "category", "amount");
new Chart(document.getElementById("categoryChart"), {
    type: "doughnut",
    data: {
        labels: categories.labels,
        datasets: [{ data: categories.amounts, backgroundColor: palette }]
    },
    options: { responsive: true, plugins: { legend: { position: "bottom" } } }
});
