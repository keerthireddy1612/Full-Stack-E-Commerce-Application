import { useDispatch } from "react-redux";
import { useSearchParams } from "react-router-dom";
import { useEffect } from "react";
import { fetchProducts, getOrdersForDashboard } from "../store/actions";

const useOrderFilter = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();

  useEffect(() => {
    const params = new URLSearchParams();

    const currentPage = searchParams.get("page")
      ? Number(searchParams.get("page"))
      : 1;
    params.set("pageNumber", currentPage - 1);

    const queryString = params.toString();
    console.log(queryString);
    dispatch(getOrdersForDashboard(queryString));
  }, [dispatch, searchParams]);
};
export default useOrderFilter;
