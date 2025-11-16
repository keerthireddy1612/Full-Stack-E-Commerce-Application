import { Avatar, Menu, MenuItem } from "@mui/material";
import { BiUser } from "react-icons/bi";
import { FaShoppingCart, FaUserShield } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { IoExitOutline } from "react-icons/io5";
import BackDrop from "./BackDrop";
import { logOutUser } from "../store/actions";

const UserMenu = () => {
  const [anchorEl, setAnchorEl] = React.useState(null);
  const { user } = useSelector((state) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const open = Boolean(anchorEl);
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const logOutHandler = () => {
    dispatch(logOutUser(navigate));
  };
  const isAdmin = user && user?.roles.includes("ROLE_ADMIN");

  return (
    <div className="relative z-30">
      <div
        className="sm:border-[1px] sm:border-slate-400 flex flex-row items-center gap-1 rounded-full cursor-pointer hover:shadow-md transition text-slate-700 "
        onClick={handleClick}
      >
        <Avatar alt="Menu" src="" />
      </div>
      <Menu
        sx={{ width: "400px" }}
        id="basic-menu"
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
        slotProps={{
          list: {
            "aria-labelledby": "basic-button",
            sx: { width: 160 },
          },
        }}
      >
        <Link to="/profile">
          <MenuItem className="flex gap-2" onClick={handleClose}>
            <BiUser className="text-1" />
            <span className="font-bold text-[16px] mt-1">{user?.username}</span>
          </MenuItem>
        </Link>

        {isAdmin && (
          <Link to="/admin">
            <MenuItem className="flex gap-2" onClick={handleClose}>
              <FaUserShield className="text-1" />
              <span className="font-bold text-[16px] mt-1">Admin Panel</span>
            </MenuItem>
          </Link>
        )}

        <Link to="/profile/orders">
          <MenuItem className="flex gap-2" onClick={handleClose}>
            <FaShoppingCart className="text-1" />
            <span className="font-semibold ">Order</span>
          </MenuItem>
        </Link>

        <MenuItem className="flex gap-2" onClick={logOutHandler}>
          <div className="font-semibold w-full flex gap-2 items-center bg-button-gradient px-4 py-1 text-white rounded-sm">
            <IoExitOutline className="text-1" />
            <span className="font-bold text-[16px] mt-1">LogOut</span>
          </div>
        </MenuItem>
      </Menu>
      {open && <BackDrop />}
    </div>
  );
};

export default UserMenu;
