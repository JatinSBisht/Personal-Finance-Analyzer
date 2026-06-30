import sys


def predict(values):
    count = len(values)
    if count == 0:
        return 0.0
    if count == 1:
        return values[0]

    x_values = list(range(1, count + 1))
    x_mean = sum(x_values) / count
    y_mean = sum(values) / count
    numerator = sum((x - x_mean) * (y - y_mean) for x, y in zip(x_values, values))
    denominator = sum((x - x_mean) ** 2 for x in x_values)
    slope = numerator / denominator if denominator else 0.0
    intercept = y_mean - slope * x_mean
    return max(intercept + slope * (count + 1), 0.0)


if __name__ == "__main__":
    csv = sys.argv[1] if len(sys.argv) > 1 else ""
    expenses = [float(item) for item in csv.split(",") if item.strip()]
    print(f"{predict(expenses):.2f}")
