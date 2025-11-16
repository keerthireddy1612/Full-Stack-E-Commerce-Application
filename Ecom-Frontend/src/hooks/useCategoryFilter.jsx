import { useDispatch } from "react-redux";
import { useEffect } from "react";
import { fetchCategories } from "../store/actions";

const useDashboardCategoryFilter = () => {
  const dispatch = useDispatch();

  useEffect(() => {
    // just call fetchCategories once — backend will use default params
    dispatch(fetchCategories());
  }, [dispatch]);
};

export default useDashboardCategoryFilter;
