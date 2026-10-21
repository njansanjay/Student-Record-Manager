
const nameInput = document.getElementById("name");
const searchInput = document.getElementById("search");
const list = document.getElementById("list");
const count = document.getElementById("count");

async function getStudents(url = "/students") {
    const response = await fetch(url);
    if (!response.ok) throw new Error("Could not load students");
    return response.json();
}

async function addStudent() {
    const name = nameInput.value.trim();

    if (!name) {
        alert("Enter a student name");
        return;
    }

    await fetch("/students", {
        method: "POST",
        body: name
    });

    nameInput.value = "";
    searchInput.value = "";
    showStudents();
}

async function searchStudents() {
    const query = searchInput.value.trim();

    const url = query
        ? "/search?q=" + encodeURIComponent(query)
        : "/students";

    showStudents(url);
}

async function deleteStudent(name) {
    await fetch("/delete", {
        method: "POST",
        body: name
    });

    showStudents();
}

async function showStudents(url = "/students") {
    try {
        const students = await getStudents(url);
        list.replaceChildren();

        students.forEach(name => {
            const item = document.createElement("li");
            const label = document.createElement("span");
            const button = document.createElement("button");

            label.textContent = name;
            button.textContent = "Delete";
            button.onclick = () => deleteStudent(name);

            item.append(label, button);
            list.appendChild(item);
        });

        count.textContent = "Matching students: " + students.length;

        if (students.length === 0) {
            const item = document.createElement("li");
            item.textContent = "No students found";
            list.appendChild(item);
        }
    } catch (error) {
        alert("Could not connect to the Java server.");
    }
}

showStudents();
