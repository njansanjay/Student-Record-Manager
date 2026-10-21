
const nameInput = document.getElementById("name");
const searchInput = document.getElementById("search");
const list = document.getElementById("list");
const count = document.getElementById("count");

let students = JSON.parse(
    localStorage.getItem("students") || "[]"
);

function saveStudents() {
    localStorage.setItem("students", JSON.stringify(students));
}

function addStudent() {
    const name = nameInput.value.trim();

    if (!name) {
        alert("Enter a student name");
        return;
    }

    if (name.length > 100) {
        alert("Name must be under 100 characters");
        return;
    }

    students.push(name);
    saveStudents();

    nameInput.value = "";
    searchStudents();
}

function searchStudents() {
    const query = searchInput.value.trim().toLowerCase();

    // DSA: Linear Search
    const found = [];

    for (const name of students) {
        if (name.toLowerCase().includes(query)) {
            found.push(name);
        }
    }

    showStudents(found);
}

function deleteStudent(index) {
    students.splice(index, 1);
    saveStudents();
    searchStudents();
}

function showStudents(results) {
    list.replaceChildren();

    count.textContent =
        "Total students: " + students.length;

    if (results.length === 0) {
        const item = document.createElement("li");
        item.textContent = "No students found";
        list.appendChild(item);
        return;
    }

    results.forEach(name => {
        const item = document.createElement("li");
        const label = document.createElement("span");
        const button = document.createElement("button");

        label.textContent = name;
        button.textContent = "Delete";

        // Delete the exact matching record.
        button.onclick = () => {
            const index = students.indexOf(name);
            if (index !== -1) deleteStudent(index);
        };

        item.append(label, button);
        list.appendChild(item);
    });
}

nameInput.addEventListener("keydown", event => {
    if (event.key === "Enter") addStudent();
});

searchStudents();
