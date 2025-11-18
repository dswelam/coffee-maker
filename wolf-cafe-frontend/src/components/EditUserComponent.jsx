import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getUserById, updateUser } from "../services/UserService";

const EditUserComponent = () => {
  const { id, type } = useParams(); // type = staff or customer
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState(type === "staff" ? "STAFF" : "CUSTOMER");

  const [errors, setErrors] = useState({});

  // Load existing user
  useEffect(() => {
    getUserById(id).then((res) => {
      setUsername(res.data.username);
      setEmail(res.data.email);
      setRole(res.data.role);
    });
  }, [id]);

  function validate() {
    const errs = {};

    if (!username.trim() || !/^[A-Za-z]+$/.test(username))
      errs.username = "Name must be non-empty and contain only letters.";

    if (!email.includes("@"))
      errs.email = "Email must contain an @ symbol.";

    if (!password.trim())
      errs.password = "Password cannot be empty.";

    setErrors(errs);
    return Object.keys(errs).length === 0;
  }

  async function submit(e) {
    e.preventDefault();
    if (!validate()) return;

    try {
      await updateUser(id, { username, email, password, role });
      navigate(type === "staff" ? "/staff" : "/customers");
    } catch (err) {
      if (err.response?.status === 409) {
        setErrors({ duplicate: "Username or email already exists." });
      } else {
        setErrors({ general: "Something went wrong." });
      }
    }
  }

  return (
    <div className="container" style={{ paddingTop: "120px" }}>
      <h2 className="text-center">{`Edit ${type === "staff" ? "Staff" : "Customer"}`}</h2>

      {errors.general && <div className="alert alert-danger">{errors.general}</div>}
      {errors.duplicate && <div className="alert alert-danger">{errors.duplicate}</div>}

      <form>
        <div className="mb-3">
          <label>Username</label>
          <input
            className="form-control"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          {errors.username && <small className="text-danger">{errors.username}</small>}
        </div>

        <div className="mb-3">
          <label>Email</label>
          <input
            className="form-control"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          {errors.email && <small className="text-danger">{errors.email}</small>}
        </div>

        <div className="mb-3">
          <label>Password</label>
          <input
            className="form-control"
            type="password"
            onChange={(e) => setPassword(e.target.value)}
          />
          {errors.password && <small className="text-danger">{errors.password}</small>}
        </div>

        <button className="btn btn-primary" onClick={submit}>
          Done
        </button>
      </form>
    </div>
  );
};

export default EditUserComponent;
