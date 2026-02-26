// Estado simples do app
const state = {
  token: localStorage.getItem("financeapp_token") || "",
  editId: null,
  authMode: "login"
};

// Referencias da interface
const ui = {
  authForm: document.getElementById("auth-form"),
  authUsername: document.getElementById("auth-username"),
  authPassword: document.getElementById("auth-password"),
  authSubmit: document.getElementById("auth-submit"),
  authStatus: document.getElementById("auth-status"),
  tabLogin: document.getElementById("tab-login"),
  tabRegister: document.getElementById("tab-register"),
  txForm: document.getElementById("transaction-form"),
  txType: document.getElementById("tx-type"),
  txAmount: document.getElementById("tx-amount"),
  txCategory: document.getElementById("tx-category"),
  txDate: document.getElementById("tx-date"),
  txDescription: document.getElementById("tx-description"),
  txSubmit: document.getElementById("tx-submit"),
  txCancel: document.getElementById("tx-cancel"),
  txStatus: document.getElementById("tx-status"),
  txTable: document.getElementById("tx-table"),
  listStatus: document.getElementById("list-status"),
  reportYear: document.getElementById("report-year"),
  reportMonth: document.getElementById("report-month"),
  reportRefresh: document.getElementById("report-refresh"),
  reportBalance: document.getElementById("report-balance"),
  reportIncome: document.getElementById("report-income"),
  reportExpense: document.getElementById("report-expense"),
  reportCategories: document.getElementById("report-categories")
};

// Feedback rapido para o usuario
function setStatus(element, message, isError = false) {
  element.textContent = message;
  element.style.color = isError ? "#f25c5c" : "#98a2b3";
}

// Formata valores em reais
function formatCurrency(value) {
  const number = Number(value || 0);
  return number.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

// Wrapper de fetch com token quando houver
async function apiRequest(path, options = {}) {
  const headers = options.headers || {};
  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }
  if (options.body) {
    headers["Content-Type"] = "application/json";
  }

  const response = await fetch(path, { ...options, headers });
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Erro na requisicao");
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

// Alterna entre login e registro
function setAuthMode(mode) {
  state.authMode = mode;
  ui.tabLogin.classList.toggle("active", mode === "login");
  ui.tabRegister.classList.toggle("active", mode === "register");
  ui.authSubmit.textContent = mode === "login" ? "Entrar" : "Registrar";
  setStatus(ui.authStatus, "");
}

// Login ou registro
async function handleAuthSubmit(event) {
  event.preventDefault();
  setStatus(ui.authStatus, "Autenticando...");

  const payload = {
    username: ui.authUsername.value.trim(),
    password: ui.authPassword.value.trim()
  };

  try {
    const endpoint = state.authMode === "login" ? "/auth/login" : "/auth/register";
    const data = await apiRequest(endpoint, {
      method: "POST",
      body: JSON.stringify(payload)
    });

    state.token = data.token;
    localStorage.setItem("financeapp_token", state.token);
    setStatus(ui.authStatus, "Autenticado com sucesso.");
    await refreshAll();
  } catch (error) {
    setStatus(ui.authStatus, error.message || "Falha na autenticacao", true);
  }
}

// Limpa o formulario depois de salvar
function resetTransactionForm() {
  state.editId = null;
  ui.txForm.reset();
  ui.txDate.valueAsDate = new Date();
  ui.txSubmit.textContent = "Salvar";
}

// Cria ou atualiza transacao
async function handleTransactionSubmit(event) {
  event.preventDefault();
  setStatus(ui.txStatus, "Salvando...");

  const payload = {
    type: ui.txType.value,
    amount: Number(ui.txAmount.value),
    category: ui.txCategory.value.trim(),
    date: ui.txDate.value,
    description: ui.txDescription.value.trim()
  };

  if (!state.token) {
    setStatus(ui.txStatus, "Faça login para registrar transações.");
    return;
  }
  try {
    if (state.editId) {
      await apiRequest(`/api/transactions/${state.editId}`, {
        method: "PUT",
        body: JSON.stringify(payload)
      });
      setStatus(ui.txStatus, "Transacao atualizada.");
    } else {
      await apiRequest("/api/transactions", {
        method: "POST",
        body: JSON.stringify(payload)
      });
      setStatus(ui.txStatus, "Transacao criada.");
    }

    resetTransactionForm();
    await refreshAll();
  } catch (error) {
    setStatus(ui.txStatus, error.message || "Falha ao salvar", true);
  }
}

// Remove transacao
async function handleDelete(id) {
  if (!confirm("Deseja remover esta transacao?")) {
    return;
  }

  try {
    await apiRequest(`/api/transactions/${id}`, { method: "DELETE" });
    await refreshAll();
  } catch (error) {
    setStatus(ui.listStatus, error.message || "Falha ao remover", true);
  }
}

// Preenche formulario para edicao
function handleEdit(transaction) {
  state.editId = transaction.id;
  ui.txType.value = transaction.type;
  ui.txAmount.value = transaction.amount;
  ui.txCategory.value = transaction.category;
  ui.txDate.value = transaction.date;
  ui.txDescription.value = transaction.description || "";
  ui.txSubmit.textContent = "Atualizar";
  setStatus(ui.txStatus, "Editando transacao.");
}

// Monta a tabela
function renderTransactions(transactions) {
  ui.txTable.innerHTML = "";

  if (!transactions.length) {
    ui.txTable.innerHTML = "<tr><td colspan=\"6\">Sem transacoes cadastradas.</td></tr>";
    return;
  }

  transactions.forEach((transaction) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${transaction.type === "INCOME" ? "Entrada" : "Saida"}</td>
      <td>${formatCurrency(transaction.amount)}</td>
      <td>${transaction.category}</td>
      <td>${transaction.date}</td>
      <td>${transaction.description || "-"}</td>
      <td>
        <button class="btn ghost" type="button" data-edit="${transaction.id}">Editar</button>
        <button class="btn ghost" type="button" data-delete="${transaction.id}">Excluir</button>
      </td>
    `;
    ui.txTable.appendChild(row);
  });

  ui.txTable.querySelectorAll("[data-edit]").forEach((button) => {
    button.addEventListener("click", () => {
      const id = Number(button.dataset.edit);
      const transaction = transactions.find((item) => item.id === id);
      if (transaction) {
        handleEdit(transaction);
      }
    });
  });

  ui.txTable.querySelectorAll("[data-delete]").forEach((button) => {
    button.addEventListener("click", () => handleDelete(Number(button.dataset.delete)));
  });
}

// Mostra saldo e categorias
function renderReport(report) {
  ui.reportBalance.textContent = formatCurrency(report.monthlyBalance || 0);
  ui.reportIncome.textContent = formatCurrency(report.totalIncome || 0);
  ui.reportExpense.textContent = formatCurrency(report.totalExpense || 0);
  ui.reportCategories.innerHTML = "";

  const entries = Object.entries(report.totalsByCategory || {});
  if (!entries.length) {
    ui.reportCategories.innerHTML = "<li>Nenhuma categoria no periodo.</li>";
    return;
  }

  entries.forEach(([category, total]) => {
    const item = document.createElement("li");
    item.textContent = `${category} - ${formatCurrency(total)}`;
    ui.reportCategories.appendChild(item);
  });
}

// Busca lista atualizada
async function refreshTransactions() {
  try {
    const data = await apiRequest("/api/transactions");
    renderTransactions(data);
    setStatus(ui.listStatus, "");
  } catch (error) {
    setStatus(ui.listStatus, error.message || "Erro ao carregar", true);
  }
}

// Busca relatorio do mes
async function refreshReport() {
  if (!state.token) {
    setStatus(ui.listStatus, "Autentique-se para ver o relatorio.");
    return;
  }

  try {
    const year = ui.reportYear.value;
    const month = ui.reportMonth.value;
    const query = year && month ? `?year=${year}&month=${month}` : "";
    const data = await apiRequest(`/api/reports${query}`);
    renderReport(data);
  } catch (error) {
    setStatus(ui.listStatus, error.message || "Erro no relatorio", true);
  }
}

// Recarrega dados protegidos
async function refreshAll() {
  if (!state.token) {
    setStatus(ui.listStatus, "Autentique-se para ver as transacoes.");
    return;
  }

  await Promise.all([refreshTransactions(), refreshReport()]);
}

// Configuracao inicial da tela
function initDefaults() {
  const now = new Date();
  ui.reportYear.value = now.getFullYear();
  ui.reportMonth.value = now.getMonth() + 1;
  ui.txDate.valueAsDate = now;

  ui.txCancel.addEventListener("click", () => {
    resetTransactionForm();
    setStatus(ui.txStatus, "Edicao cancelada.");
  });

  ui.tabLogin.addEventListener("click", () => setAuthMode("login"));
  ui.tabRegister.addEventListener("click", () => setAuthMode("register"));
  ui.authForm.addEventListener("submit", handleAuthSubmit);
  ui.txForm.addEventListener("submit", handleTransactionSubmit);
  ui.reportRefresh.addEventListener("click", refreshReport);

  setAuthMode("login");
  if (state.token) {
    refreshAll();
  } else {
    setStatus(ui.listStatus, "Autentique-se para ver as transacoes.");
  }
}

document.addEventListener("DOMContentLoaded", initDefaults);
